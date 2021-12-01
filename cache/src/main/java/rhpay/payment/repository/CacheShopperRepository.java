package rhpay.payment.repository;

import org.infinispan.Cache;
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
        ShopperEntity shopperEntity = shopperCache.get(new ShopperKey(id.value));
        return new Shopper(id, new FullName(shopperEntity.getName()));
    }
}
