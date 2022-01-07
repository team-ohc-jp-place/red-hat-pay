package rhpay.payment.repository;

import io.quarkus.infinispan.client.Remote;
import org.infinispan.client.hotrod.RemoteCache;
import rhpay.payment.cache.ShopperEntity;
import rhpay.payment.cache.ShopperKey;
import rhpay.payment.domain.FullName;
import rhpay.payment.domain.Shopper;
import rhpay.payment.domain.ShopperId;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class CacheShopperRepository implements ShopperRepository {

    @Inject
    @Remote("user")
    RemoteCache<ShopperKey, ShopperEntity> userCache;

    public Shopper load(final ShopperId id) {
        ShopperEntity shopperEntity = userCache.get(new ShopperKey(id.value));
        Shopper shopper = new Shopper(id, new FullName(shopperEntity.getName()));
        return shopper;
    }
}
