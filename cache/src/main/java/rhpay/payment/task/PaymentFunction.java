package rhpay.payment.task;

import jdk.jfr.Event;
import org.infinispan.AdvancedCache;
import org.infinispan.Cache;
import org.infinispan.container.entries.ImmortalCacheEntry;
import org.infinispan.context.Flag;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.util.function.SerializableBiConsumer;
import rhpay.monitoring.EntryListener;
import rhpay.monitoring.TransactionListener;
import rhpay.monitoring.event.DistributedTaskEvent;
import rhpay.payment.cache.*;
import rhpay.payment.domain.*;
import rhpay.payment.repository.CachePaymentRepository;
import rhpay.payment.repository.CacheShopperRepository;
import rhpay.payment.repository.CacheTokenRepository;
import rhpay.payment.repository.CacheWalletRepository;
import rhpay.payment.service.PaymentService;
import rhpay.payment.service.ShopperService;
import rhpay.payment.service.TokenService;
import rhpay.payment.service.WalletService;

import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import java.util.ArrayList;
import java.util.List;

public class PaymentFunction implements SerializableBiConsumer<Cache<ShopperKey, WalletEntity>, ImmortalCacheEntry> {

    @ProtoField(number = 1, defaultValue = "0")
    final int shopperId;

    @ProtoField(number = 2)
    final String tokenId;

    @ProtoField(number = 3, defaultValue = "0")
    final int amount;

    @ProtoField(number = 4, defaultValue = "0")
    final int storeId;

    @ProtoField(number = 5)
    final String storeName;

    @ProtoField(number = 6)
    final String traceId;

    @ProtoFactory
    public PaymentFunction(int shopperId, String tokenId, int amount, int storeId, String storeName, String traceId) {
        this.shopperId = shopperId;
        this.tokenId = tokenId;
        this.amount = amount;
        this.storeId = storeId;
        this.storeName = storeName;
        this.traceId = traceId;
    }

    private final int MAX_RETRY_COUNT = 3;

    @Override
    public void accept(Cache<ShopperKey, WalletEntity> walletCache, ImmortalCacheEntry entry) {
        try {
            EmbeddedCacheManager cacheManager = walletCache.getCacheManager();
            AdvancedCache<TokenKey, TokenEntity> advancedTokenCache = cacheManager.<TokenKey, TokenEntity>getCache("token").getAdvancedCache();
            AdvancedCache<ShopperKey, WalletEntity> advancedWalletCache = walletCache.getAdvancedCache().withFlags(Flag.FORCE_WRITE_LOCK);
            Cache<TokenKey, PaymentEntity> paymentCache = cacheManager.getCache("payment");
            Cache<ShopperKey, ShopperEntity> shopperCache = cacheManager.getCache("user");
            advancedTokenCache.addListener(EntryListener.getInstance());
            advancedTokenCache.addListener(TransactionListener.getInstance());
            advancedWalletCache.addListener(EntryListener.getInstance());
            advancedWalletCache.addListener(TransactionListener.getInstance());
            paymentCache.addListener(EntryListener.getInstance());
            paymentCache.addListener(TransactionListener.getInstance());
            shopperCache.addListener(EntryListener.getInstance());
            shopperCache.addListener(TransactionListener.getInstance());

            TransactionManager transactionManager = walletCache.getAdvancedCache().getTransactionManager();

            WalletService walletService = new WalletService(new CacheWalletRepository(advancedWalletCache));
            ShopperService shopperService = new ShopperService(new CacheShopperRepository(shopperCache));
            TokenService tokenService = new TokenService(new CacheTokenRepository(advancedTokenCache));
            PaymentService paymentService = new PaymentService(new CachePaymentRepository(paymentCache));

            final ShopperId shopperId = new ShopperId(this.shopperId);
            final TokenId tokenId = new TokenId(this.tokenId);
            final Money amount = new Money(this.amount);
            final StoreId storeId = new StoreId(this.storeId);
            final StoreName storeName = new StoreName(this.storeName);
            final CoffeeStore store = new CoffeeStore(storeId, storeName);

            List<Exception> retryCauseList = null;
            boolean success = false;

            for (int tryCount = 1; tryCount <= MAX_RETRY_COUNT; tryCount++) {
                // Distributed Streamを実行している記録
                Event taskEvent = new DistributedTaskEvent("PaymentTask", shopperId.value, tokenId.value, traceId, tryCount);
                taskEvent.begin();

                final Billing bill = store.createBill(amount);

                try {
                    // トランザクションを開始する
                    transactionManager.begin();

                    // 買い物客の情報を読み込む
                    Shopper shopper = shopperService.load(shopperId);

                    Wallet wallet = walletService.load(shopper);

                    // トークンを読み込む
                    Token token = tokenService.load(shopperId, tokenId);
                    if (token == null) {
                        throw new RuntimeException(String.format("This token is not exist : [%s, %s]", shopperId, tokenId));
                    }

                    // トークンを処理中にする
                    token = token.processing();
                    tokenService.store(token);

                    // 請求処理
                    Payment payment = wallet.pay(bill, tokenId);

                    // トークンを使用済みにする
                    token = token.used();
                    tokenService.store(token);

                    // 支払い結果を格納する
                    paymentService.store(payment);

                    walletService.store(wallet);

                    transactionManager.commit();
                    success = true;

                } catch (Exception e) {
                    if (tryCount == 1) {
                        retryCauseList = new ArrayList(MAX_RETRY_COUNT);
                    }
                    retryCauseList.add(e);
                    try {
                        transactionManager.rollback();
                    } catch (SystemException ex) {
                        e.addSuppressed(ex);
                    }
                } finally {
                    taskEvent.commit();
                }

                if(success) break;
            }

            advancedTokenCache.removeListener(EntryListener.getInstance());
            advancedTokenCache.removeListener(TransactionListener.getInstance());
            advancedWalletCache.removeListener(EntryListener.getInstance());
            advancedWalletCache.removeListener(TransactionListener.getInstance());
            paymentCache.removeListener(EntryListener.getInstance());
            paymentCache.removeListener(TransactionListener.getInstance());
            shopperCache.removeListener(EntryListener.getInstance());
            shopperCache.removeListener(TransactionListener.getInstance());

            if (!success) {
                RuntimeException retryFailExeption = new RuntimeException("Fail to pay");
                retryCauseList.stream().forEach(e -> retryFailExeption.addSuppressed(e));
                throw retryFailExeption;
            }
        }catch(RuntimeException e){
            e.printStackTrace();
            throw e;
        }
    }
}
