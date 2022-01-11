package rhpay.point.repository;

import rhpay.payment.domain.Money;
import rhpay.payment.domain.ShopperId;
import rhpay.point.domain.Point;
import org.infinispan.Cache;
import rhpay.payment.cache.ShopperKey;
import rhpay.point.cache.PointEntity;
import rhpay.point.repository.function.PointAddFunction;

public class CachePointRepository implements PoindAddRepository {

    private final Cache<ShopperKey, PointEntity> pointCache;

    public CachePointRepository(Cache<ShopperKey, PointEntity> pointCache) {
        this.pointCache = pointCache;
    }

    /**
     * ポイントを加算する処理
     *
     * @param paidAmount
     * @return
     */
    public Point addPoint(ShopperId shopperId, Money paidAmount) {

        PointAddFunction pointTask = new PointAddFunction(paidAmount.value);

        ShopperKey key = new ShopperKey(shopperId.value);

        PointEntity newCachedData = pointCache.computeIfPresent(key, pointTask);

        return new Point(shopperId, newCachedData.getAmount());
    }
}
