package rhpay.point.repository;

import rhpay.payment.domain.Payment;
import rhpay.point.domain.Point;
import jdk.jfr.Event;
import org.infinispan.Cache;
import org.infinispan.util.function.SerializableBiFunction;
import rhpay.monitoring.ShopperFunctionEvent;
import rhpay.payment.cache.ShopperKey;
import rhpay.point.cache.PointEntity;

public class CachePointRepository implements PointRepository {

    private final Cache<ShopperKey, PointEntity> pointCache;

    public CachePointRepository(Cache<ShopperKey, PointEntity> pointCache) {
        this.pointCache = pointCache;
    }

    /**
     * ポイントを加算する処理
     *
     * @param payment
     * @return
     */
    public Point givePoint(Payment payment) {

        PointAddFunction pointTask = new PointAddFunction(payment);

        ShopperKey key = new ShopperKey(payment.getShopperId().value);

        PointEntity newCachedData = pointCache.computeIfPresent(key, pointTask);

        return new Point(payment.getShopperId(), newCachedData.getAmount());
    }
}

class PointAddFunction implements SerializableBiFunction<ShopperKey, PointEntity, PointEntity> {

    private final Payment payment;

    public PointAddFunction(Payment payment) {
        this.payment = payment;
    }

    @Override
    public PointEntity apply(ShopperKey pointKey, PointEntity cachedPointData) {

        Event functionEvent = new ShopperFunctionEvent("PointAddFunction", pointKey.getOwnerId());
        functionEvent.begin();

        try {
            // キャッシュされたオブジェクトからドメインのオブジェクトを作成
            Point currentPoint = new Point(payment.getShopperId(), cachedPointData.getAmount());

            // 支払われた金額からポイントを加算する処理
            Point newPoint = currentPoint.addPoint(payment);

            // レスポンスを返す
            return new PointEntity(newPoint.getPoint());
        } finally {
            functionEvent.commit();
        }
    }
}

