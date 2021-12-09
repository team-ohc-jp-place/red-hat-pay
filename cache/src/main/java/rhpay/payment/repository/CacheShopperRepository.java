package rhpay.payment.repository;

import jdk.jfr.Event;
import org.infinispan.Cache;
import rhpay.monitoring.CacheUseEvent;
import rhpay.monitoring.SegmentService;
import rhpay.payment.cache.ShopperEntity;
import rhpay.payment.cache.ShopperKey;
import rhpay.payment.domain.FullName;
import rhpay.payment.domain.Shopper;
import rhpay.payment.domain.ShopperId;

public class CacheShopperRepository implements ShopperRepository{

    private final Cache<ShopperKey, ShopperEntity> shopperCache;

    public CacheShopperRepository(Cache<ShopperKey, ShopperEntity> shopperCache) {
        this.shopperCache = shopperCache;
    }

    @Override
    public Shopper load(ShopperId id) {
        ShopperKey key = new ShopperKey(id.value);
        Event event = new CacheUseEvent(SegmentService.getSegment(shopperCache, key), "loadShopper");
        event.begin();
        ShopperEntity shopperEntity = shopperCache.get(key);
        event.commit();
        return new Shopper(id, new FullName(shopperEntity.getName()));
    }
}
