package rhpay.point.task;

import rhpay.payment.domain.*;
import rhpay.point.PointAddService;
import rhpay.point.cache.PointEntity;
import rhpay.payment.cache.ShopperKey;
import rhpay.monitoring.event.TaskEvent;
import rhpay.point.domain.Point;
import rhpay.point.repository.CachePointRepository;
import rhpay.point.repository.PoindAddRepository;
import jdk.jfr.Event;
import org.infinispan.Cache;
import org.infinispan.tasks.ServerTask;
import org.infinispan.tasks.TaskContext;

import javax.transaction.TransactionManager;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

// TODO: Distributed Streamに対応させる

/**
 * Distributed Streamはまだ非対応
 */
public class PointTask implements ServerTask<PointEntity> {


    private final ThreadLocal<PointTaskParameter> parameterThreadLocal = new ThreadLocal<>();

    @Override
    public void setTaskContext(TaskContext taskContext) {

        Cache<ShopperKey, PointEntity> pointCache = (Cache<ShopperKey, PointEntity>) taskContext.getCache().get();
        PoindAddRepository pointRepository = new CachePointRepository(pointCache);
        PointAddService pointService = new PointAddService(pointRepository);

        Map<String, ?> param = taskContext.getParameters().get();

        String traceId = (String) param.get("traceId");
        StoreId storeId = new StoreId((Integer) param.get("storeId"));
        ShopperId shopperId = new ShopperId((Integer) param.get("ownerId"));
        TokenId tokenId = new TokenId((String) param.get("tokenId"));
        Money billAmount = new Money((Integer) param.get("amount"));
        final LocalDateTime dateTime = LocalDateTime.ofEpochSecond((Long) param.get("epoch"), 0, ZoneOffset.of("+09:00"));
        Payment payment = new Payment(storeId, shopperId, billAmount, dateTime);

        PointTaskParameter parameter = new PointTaskParameter(traceId, payment, pointService, pointCache);
        parameterThreadLocal.set(parameter);
    }

    @Override
    public PointEntity call() throws Exception {

        PointTaskParameter parameter = parameterThreadLocal.get();
        String traceId = parameter.traceId;

        // ドメインのオブジェクトを作成
        Payment payment = parameter.payment;

        // ポイントを加算する
        PointAddService pointService = parameter.pointService;

        TransactionManager transactionManager = parameter.pointCache.getAdvancedCache().getTransactionManager();

        Event taskEvent = new TaskEvent(traceId, "PointTask", parameter.payment.getShopperId().value);
        taskEvent.begin();
        try {
            // トランザクションを開始する
            transactionManager.begin();

            final Point newPoint = pointService.addPoint(payment.getShopperId(), payment.getBillingAmount());

            transactionManager.commit();
            // レスポンスを返す
            return new PointEntity(newPoint.getPoint());
        } catch (Exception e) {
            e.printStackTrace();
            transactionManager.rollback();
            throw e;
        } finally {
            taskEvent.commit();
            parameterThreadLocal.remove();
        }
    }

    @Override
    public String getName() {
        return "PointTask";
    }

}

class PointTaskParameter {

    public final String traceId;
    public final Payment payment;
    public final PointAddService pointService;
    public final Cache<ShopperKey, PointEntity> pointCache;

    public PointTaskParameter(String traceId, Payment payment, PointAddService pointService, Cache<ShopperKey, PointEntity> pointCache) {
        this.traceId = traceId;
        this.payment = payment;
        this.pointService = pointService;
        this.pointCache = pointCache;
    }
}
