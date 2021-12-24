package rhpay.payment.task;

import jdk.jfr.Event;
import org.infinispan.AdvancedCache;
import org.infinispan.Cache;
import org.infinispan.container.entries.ImmortalCacheEntry;
import org.infinispan.container.entries.metadata.MetadataImmortalCacheEntry;
import org.infinispan.context.Flag;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.util.function.SerializableBiConsumer;
import rhpay.monitoring.DistributedTaskEvent;
import rhpay.payment.cache.*;
import rhpay.payment.domain.*;
import rhpay.payment.repository.CacheBillingRepository;
import rhpay.payment.repository.CachePaymentRepository;
import rhpay.payment.repository.CacheShopperRepository;
import rhpay.payment.repository.CacheTokenRepository;
import rhpay.payment.service.BillingService;
import rhpay.payment.service.PaymentService;
import rhpay.payment.service.ShopperService;
import rhpay.payment.service.TokenService;

import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

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

    @ProtoFactory
    public PaymentFunction(int shopperId, String tokenId, int amount, int storeId, String storeName) {
        this.shopperId = shopperId;
        this.tokenId = tokenId;
        this.amount = amount;
        this.storeId = storeId;
        this.storeName = storeName;
    }

    @Override
    public void accept(Cache<ShopperKey, WalletEntity> walletCache, ImmortalCacheEntry entry) {
        EmbeddedCacheManager cacheManager = walletCache.getCacheManager();
        Cache<TokenKey, TokenEntity> tokenCache = cacheManager.getCache("token");
        AdvancedCache<TokenKey, TokenEntity> advancedTokenCache = tokenCache.getAdvancedCache().withFlags(Flag.FORCE_WRITE_LOCK);
        Cache<TokenKey, PaymentEntity> paymentCache = cacheManager.getCache("payment");
        Cache<ShopperKey, ShopperEntity> shopperCache = cacheManager.getCache("user");

        TransactionManager transactionManager = walletCache.getAdvancedCache().getTransactionManager();

        BillingService billingService = new BillingService(new CacheBillingRepository(walletCache));
        ShopperService shopperService = new ShopperService(new CacheShopperRepository(shopperCache));
        TokenService tokenService = new TokenService(new CacheTokenRepository(advancedTokenCache));
        PaymentService paymentService = new PaymentService(new CachePaymentRepository(paymentCache));

        final ShopperId shopperId = new ShopperId(this.shopperId);
        final TokenId tokenId = new TokenId(this.tokenId);
        final Money amount = new Money(this.amount);
        final StoreId storeId = new StoreId(this.storeId);
        final StoreName storeName = new StoreName(this.storeName);
        final CoffeeStore store = new CoffeeStore(storeId, storeName);

        // JFRのイベントを開始する
        Event taskEvent = new DistributedTaskEvent("PaymentTask", shopperId.value, tokenId.value);
        taskEvent.begin();
        Token token = null;

        final Billing bill = store.createBill(amount);

        try {
            // トランザクションを開始する
            transactionManager.begin();

            // 買い物客の情報を読み込む
            Shopper shopper = shopperService.load(shopperId);

            // トークンを読み込んで処理中にする
            token = tokenService.load(shopperId, tokenId);
            token = tokenService.processing(token);

            // 請求処理
            final Payment payment = billingService.bill(shopper, tokenId, bill);

            // トークンを使用済みにする
            token = tokenService.used(token);

            // 支払い結果を格納する
            paymentService.store(payment);

            transactionManager.commit();

        } catch (Exception e) {
            RuntimeException thrw = new RuntimeException("Fail to pay");
            thrw.addSuppressed(e);
            try {
                transactionManager.rollback();
            } catch (SystemException ex) {
                thrw.addSuppressed(ex);
            }
            if (token == null) {
                thrw = new RuntimeException(String.format("This token is not exist : [%s, %s]", shopperId, tokenId));
            } else {
                try {
                    tokenService.failed(token);
                } catch (Exception e1) {
                    thrw.addSuppressed(e1);
                }
            }
            thrw.printStackTrace();
            throw thrw;
        } finally {
            taskEvent.commit();
        }
    }
}
