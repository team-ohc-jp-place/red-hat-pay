package rhpay.payment.task;

import jdk.jfr.Event;
import org.infinispan.Cache;
import org.infinispan.CacheStream;
import org.infinispan.container.entries.metadata.MetadataImmortalCacheEntry;
import org.infinispan.tasks.ServerTask;
import org.infinispan.tasks.TaskContext;
import org.infinispan.util.function.SerializableBiConsumer;
import rhpay.monitoring.CacheUseEvent;
import rhpay.monitoring.CallDistributedTaskEvent;
import rhpay.monitoring.SegmentService;
import rhpay.monitoring.TaskEvent;
import rhpay.payment.cache.*;

import java.util.Map;
import java.util.Set;

/**
 * 支払時のサーバタスク
 */
public class PaymentTask implements ServerTask<PaymentResponse> {

    private final ThreadLocal<PaymentDistParam> parameterThreadLocal = new ThreadLocal<>();

    @Override
    public void setTaskContext(TaskContext taskContext) {

        Map<String, ?> receivedParam = taskContext.getParameters().get();
        final int shopperId = (Integer) receivedParam.get("shopperId");
        final String tokenId = (String) receivedParam.get("tokenId");
        final int amount = (Integer) receivedParam.get("amount");
        final int storeId = (Integer) receivedParam.get("storeId");
        final String storeName = (String) receivedParam.get("storeName");
        Cache<ShopperKey, WalletEntity> cache = (Cache<ShopperKey, WalletEntity>) taskContext.getCache().get();

        PaymentDistParam parameter = new PaymentDistParam(shopperId, tokenId, amount, storeId, storeName, cache);
        parameterThreadLocal.set(parameter);
    }

    @Override
    public PaymentResponse call() throws Exception {

        PaymentDistParam parameter = parameterThreadLocal.get();

        final int shopperId = parameter.shopperId;
        final String tokenId = parameter.tokenId;
        final int amount = parameter.amount;
        final int storeId = parameter.storeId;
        final String storeName = parameter.storeName;

        Event taskEvent = new TaskEvent("PaymentTask", shopperId, tokenId);
        taskEvent.begin();

        try {
            // データのあるところで処理を実行する
            ShopperKey key = new ShopperKey(parameter.shopperId);
            PaymentFunction paymentFunction = new PaymentFunction(shopperId, tokenId, amount, storeId, storeName);
            Cache<ShopperKey, WalletEntity> walletCache = parameter.cache;
            CacheStream cacheStream = walletCache.entrySet().stream();
            Event distributedTaskEvent = new CallDistributedTaskEvent(SegmentService.getSegment(walletCache, key), "paymentFunction");
            try {
                distributedTaskEvent.begin();
                cacheStream.filterKeys(Set.of(key)).forEach(paymentFunction);
            } finally {
                distributedTaskEvent.commit();
            }

            // 処理の結果を取得する
            Cache<TokenKey, PaymentEntity> paymentCache = walletCache.getCacheManager().getCache("payment");
            TokenKey tokenKey = new TokenKey(shopperId, tokenId);
            Event cacheUseEvent = new CacheUseEvent(SegmentService.getSegment(paymentCache, key), "getPayment");
            try {
                cacheUseEvent.begin();
                PaymentEntity paymentEntity = paymentCache.get(tokenKey);
                return new PaymentResponse(storeId, shopperId, tokenId, paymentEntity.getBillingAmount(), paymentEntity.getBillingDateTime());
            } finally {
                cacheUseEvent.commit();
            }
        } finally {
            taskEvent.commit();
        }
    }

    @Override
    public String getName() {
        return "PaymentTask";
    }

}

/**
 * クライアントから送られてきたパラメータをスレッドローカルに一括して格納する為のクラス
 */
class PaymentDistParam {

    final int shopperId;

    final String tokenId;

    final int amount;

    final int storeId;

    final String storeName;

    final Cache<ShopperKey, WalletEntity> cache;

    public PaymentDistParam(int shopperId, String tokenId, int amount, int storeId, String storeName, Cache<ShopperKey, WalletEntity> cache) {
        this.shopperId = shopperId;
        this.tokenId = tokenId;
        this.amount = amount;
        this.storeId = storeId;
        this.storeName = storeName;
        this.cache = cache;
    }
}