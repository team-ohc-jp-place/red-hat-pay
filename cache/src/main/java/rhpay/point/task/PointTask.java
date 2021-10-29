package rhpay.point.task;

import rhpay.payment.domain.*;
import rhpay.point.cache.PointEntity;
import rhpay.payment.cache.ShopperKey;
import rhpay.monitoring.TaskEvent;
import rhpay.point.domain.Point;
import rhpay.point.repository.CachePointRepository;
import rhpay.point.repository.PointRepository;
import rhpay.point.service.PointService;
import jdk.jfr.Event;
import org.infinispan.Cache;
import org.infinispan.tasks.ServerTask;
import org.infinispan.tasks.TaskContext;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

/**
 *
 */
public class PointTask implements ServerTask<PointEntity> {

    private final ThreadLocal<PointTaskParameter> parameterThreadLocal = new ThreadLocal<>();

    @Override
    public void setTaskContext(TaskContext taskContext) {

        PointRepository pointRepository = new CachePointRepository((Cache<ShopperKey, PointEntity>) taskContext.getCache().get());
        PointService pointService = new PointService(pointRepository);

        Map<String, ?> param = taskContext.getParameters().get();

        StoreId storeId = new StoreId((Integer) param.get("storeId"));
        ShopperId shopperId = new ShopperId((Integer) param.get("ownerId"));
        TokenId tokenId = new TokenId((String) param.get("tokenId"));
        Money billAmount = new Money((Integer) param.get("amount"));
        final LocalDateTime dateTime = LocalDateTime.ofEpochSecond((Long) param.get("epoch"), 0, ZoneOffset.of("+09:00"));
        Payment payment = new Payment(storeId, shopperId, tokenId, billAmount, dateTime);

        PointTaskParameter parameter = new PointTaskParameter(payment, pointService);
        parameterThreadLocal.set(parameter);
    }

    @Override
    public PointEntity call() throws Exception {

        PointTaskParameter parameter = parameterThreadLocal.get();

        Event taskEvent = new TaskEvent("PointTask", parameter.payment.getShopperId().value, parameter.payment.getTokenId().value);
        taskEvent.begin();
        try {
            // ドメインのオブジェクトを作成
            Payment payment = parameter.payment;

            // ポイントを加算する
            PointService pointService = parameter.pointService;
            final Point newPoint = pointService.givePoint(payment);

            // レスポンスを返す
            return new PointEntity(newPoint.getPoint());
        } catch (Exception e) {
            e.printStackTrace();
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

    public final Payment payment;
    public final PointService pointService;

    public PointTaskParameter(Payment payment, PointService pointService) {
        this.payment = payment;
        this.pointService = pointService;
    }
}
