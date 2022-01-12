package rhpay.payment.task;

import jdk.jfr.Event;
import org.infinispan.Cache;
import org.infinispan.CacheStream;
import org.infinispan.tasks.ServerTask;
import org.infinispan.tasks.TaskContext;
import rhpay.monitoring.event.CallDistributedTaskEvent;
import rhpay.monitoring.SegmentService;
import rhpay.monitoring.event.TaskEvent;
import rhpay.payment.cache.PaymentResponse;
import rhpay.payment.cache.ShopperKey;
import rhpay.payment.cache.WalletEntity;
import rhpay.payment.domain.Payment;
import rhpay.payment.domain.ShopperId;
import rhpay.payment.domain.TokenId;
import rhpay.payment.repository.CachePaymentRepository;
import rhpay.payment.service.PaymentService;

import java.time.ZoneOffset;
import java.util.Map;
import java.util.Set;

/**
 * 支払時のサーバタスク
 */
public class PaymentTask implements ServerTask<PaymentResponse> {

    private final ThreadLocal<PaymentDistParam> parameterThreadLocal = new ThreadLocal<>();

    @Override
    public void setTaskContext(TaskContext taskContext) {
        try {
            Map<String, ?> receivedParam = taskContext.getParameters().get();
            final String traceId = (String) receivedParam.get("traceId");
            final int shopperId = (Integer) receivedParam.get("shopperId");
            final String tokenId = (String) receivedParam.get("tokenId");
            final int amount = (Integer) receivedParam.get("amount");
            final int storeId = (Integer) receivedParam.get("storeId");
            final String storeName = (String) receivedParam.get("storeName");
            Cache<ShopperKey, WalletEntity> cache = (Cache<ShopperKey, WalletEntity>) taskContext.getCache().get();

            PaymentDistParam parameter = new PaymentDistParam(traceId, shopperId, tokenId, amount, storeId, storeName, cache);
            parameterThreadLocal.set(parameter);
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public PaymentResponse call() throws Exception {
        try {
            PaymentDistParam parameter = parameterThreadLocal.get();

            final String traceId = parameter.traceId;
            final int shopperId = parameter.shopperId;
            final String tokenId = parameter.tokenId;
            final int amount = parameter.amount;
            final int storeId = parameter.storeId;
            final String storeName = parameter.storeName;

            Event taskEvent = new TaskEvent(traceId, "PaymentTask", shopperId, tokenId);
            taskEvent.begin();

            try {
                // データのあるところで処理を実行する
                ShopperKey key = new ShopperKey(parameter.shopperId);
                PaymentFunction paymentFunction = new PaymentFunction(shopperId, tokenId, amount, storeId, storeName, traceId);
                Cache<ShopperKey, WalletEntity> walletCache = parameter.cache;
                CacheStream cacheStream = walletCache.entrySet().stream();

                // Distributed Streamを呼び出す側の記録
                Event distributedTaskEvent = new CallDistributedTaskEvent(SegmentService.getSegment(walletCache, key), "paymentFunction");
                distributedTaskEvent.begin();

                try {
                    cacheStream.filterKeys(Set.of(key)).forEach(paymentFunction);
                } finally {
                    distributedTaskEvent.commit();
                }

                // 処理の結果を取得する
                PaymentService paymentService = new PaymentService(new CachePaymentRepository(walletCache.getCacheManager().getCache("payment")));
                Payment payment = paymentService.load(new ShopperId(shopperId), new TokenId(tokenId));
                return new PaymentResponse(storeId, shopperId, tokenId, payment.getBillingAmount().value, payment.getBillingDateTime().toEpochSecond(ZoneOffset.of("+09:00")));

            } finally {
                taskEvent.commit();
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
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

    final String traceId;

    final int shopperId;

    final String tokenId;

    final int amount;

    final int storeId;

    final String storeName;

    final Cache<ShopperKey, WalletEntity> cache;

    public PaymentDistParam(String traceId, int shopperId, String tokenId, int amount, int storeId, String storeName, Cache<ShopperKey, WalletEntity> cache) {
        this.traceId = traceId;
        this.shopperId = shopperId;
        this.tokenId = tokenId;
        this.amount = amount;
        this.storeId = storeId;
        this.storeName = storeName;
        this.cache = cache;
    }
}