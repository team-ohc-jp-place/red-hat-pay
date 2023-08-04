package rhpay.payment.task;

import jdk.jfr.Event;
import org.infinispan.AdvancedCache;
import org.infinispan.Cache;
import org.infinispan.container.entries.ImmortalCacheEntry;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.util.function.SerializableBiConsumer;
import rhpay.monitoring.event.DistributedTaskEvent;
import rhpay.payment.cache.*;
import rhpay.payment.domain.*;
import rhpay.payment.repository.*;
import rhpay.payment.usecase.TokenPayInput;
import rhpay.payment.usecase.TokenUsecase;

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
            // 使用するキャッシュを取得する
            EmbeddedCacheManager cacheManager = walletCache.getCacheManager();
            AdvancedCache<TokenKey, TokenEntity> advancedTokenCache = cacheManager.<TokenKey, TokenEntity>getCache("token").getAdvancedCache();
            AdvancedCache<ShopperKey, WalletEntity> advancedWalletCache = walletCache.getAdvancedCache();
            Cache<TokenKey, PaymentEntity> paymentCache = cacheManager.getCache("payment");
            Cache<ShopperKey, ShopperEntity> shopperCache = cacheManager.getCache("user");
/*
            advancedTokenCache.addListener(EntryListener.getInstance());
            advancedTokenCache.addListener(TransactionListener.getInstance());
            advancedWalletCache.addListener(EntryListener.getInstance());
            advancedWalletCache.addListener(TransactionListener.getInstance());
            paymentCache.addListener(EntryListener.getInstance());
            paymentCache.addListener(TransactionListener.getInstance());
            shopperCache.addListener(EntryListener.getInstance());
            shopperCache.addListener(TransactionListener.getInstance());
*/

            // リポジトリを作成
            WalletRepository walletRepository = new CacheWalletRepository(advancedWalletCache);
            ShopperRepository shopperRepository = new CacheShopperRepository(shopperCache);
            TokenRepository tokenRepository = new CacheTokenRepository(advancedTokenCache);
            PaymentRepository paymentRepository = new CachePaymentRepository(paymentCache);

            // Infinispan クライアントから受け取った情報をドメインの情報に変換
            final ShopperId shopperId = new ShopperId(this.shopperId);
            final TokenId tokenId = new TokenId(this.tokenId);
            final Money amount = new Money(this.amount);
            final StoreId storeId = new StoreId(this.storeId);

            // ストアの情報はクライアントから渡されている物を使う
            final StoreName storeName = new StoreName(this.storeName);
            final CoffeeStore store = new CoffeeStore(storeId, storeName);
            CoffeeStoreRepository coffeeStoreRepository = new DoNothingCoffeeStoreRepository(store);

            // ドメイン処理に必要なものを作成
            TokenPayInput input = new TokenPayInput(tokenRepository, coffeeStoreRepository, shopperRepository, paymentRepository, walletRepository, shopperId, amount, tokenId, storeId);
            TokenUsecase usecase = new TokenUsecase();

            //
            List<Exception> retryCauseList = null;
            boolean success = false;

            // 処理が失敗しても再実行する
            for (int tryCount = 1; tryCount <= MAX_RETRY_COUNT; tryCount++) {
                // トランザクションを取得
                TransactionManager transactionManager = walletCache.getAdvancedCache().getTransactionManager();

                // 1回分の処理のメトリクスを記録
                Event taskEvent = new DistributedTaskEvent("PaymentTask", shopperId.value, tokenId.value, traceId, tryCount);
                taskEvent.begin();

                try {
                    // トランザクションを開始する
                    transactionManager.begin();

                    // 業務処理を実行
                    Payment payment = usecase.pay(input);

                    // 成功したのでトランザクションをコミット
                    transactionManager.commit();
                    success = true;

                } catch (Exception e) {
                    // はじめて失敗した場合は例外をためる List を作る
                    if (tryCount == 1) {
                        retryCauseList = new ArrayList(MAX_RETRY_COUNT);
                    }

                    // 例外をためる
                    retryCauseList.add(e);

                    // 失敗したのでトランザクションをロールバック
                    try {
                        transactionManager.rollback();
                    } catch (Exception ex) {
                        e.addSuppressed(ex);
                    }
                } finally {
                    // メトリクスの取得を終了
                    if(taskEvent.shouldCommit()) {
                        taskEvent.commit();
                    }
                }

                // 処理が成功したら繰り返しを抜ける
                if (success) break;
            }
/*
            advancedTokenCache.removeListener(EntryListener.getInstance());
            advancedTokenCache.removeListener(TransactionListener.getInstance());
            advancedWalletCache.removeListener(EntryListener.getInstance());
            advancedWalletCache.removeListener(TransactionListener.getInstance());
            paymentCache.removeListener(EntryListener.getInstance());
            paymentCache.removeListener(TransactionListener.getInstance());
            shopperCache.removeListener(EntryListener.getInstance());
            shopperCache.removeListener(TransactionListener.getInstance());
*/
            // 最終的に失敗した場合は例外を１つにまとめて投げる
            if (!success) {
                RuntimeException retryFailExeption = new RuntimeException("Fail to pay");
                retryCauseList.stream().forEach(e -> retryFailExeption.addSuppressed(e));
                throw retryFailExeption;
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void addCacheListener(){

    }

    private void removeCacheListener(){

    }
}
