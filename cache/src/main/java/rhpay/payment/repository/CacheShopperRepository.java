package rhpay.payment.repository;

import jdk.jfr.Event;
import org.infinispan.Cache;
import rhpay.monitoring.SegmentService;
import rhpay.monitoring.UseShopperEvent;
import rhpay.payment.cache.ShopperEntity;
import rhpay.payment.cache.ShopperKey;
import rhpay.payment.domain.FullName;
import rhpay.payment.domain.Shopper;
import rhpay.payment.domain.ShopperId;

public class CacheShopperRepository implements ShopperRepository {

    private final Cache<ShopperKey, ShopperEntity> shopperCache;

    public CacheShopperRepository(Cache<ShopperKey, ShopperEntity> shopperCache) {
        this.shopperCache = shopperCache;
    }

    @Override
    public Shopper load(ShopperId id) {
        ShopperKey key = new ShopperKey(id.value);

        Event event = new UseShopperEvent("loadShopper", id.value, SegmentService.getSegment(shopperCache, key));
        event.begin();

        try {
            ShopperEntity shopperEntity = shopperCache.get(key);
            return new Shopper(id, new FullName(shopperEntity.getName()));

        } finally {
            event.commit();
        }
    }
}
