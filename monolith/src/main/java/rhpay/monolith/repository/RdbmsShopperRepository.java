package rhpay.monolith.repository;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import rhpay.monolith.entity.ShopperEntity;
import rhpay.monolith.repository.spring.ShopperSpringRepository;
import rhpay.payment.domain.FullName;
import rhpay.payment.domain.Shopper;
import rhpay.payment.domain.ShopperId;
import rhpay.payment.repository.ShopperRepository;

@Component
@RequestScope
public class RdbmsShopperRepository implements ShopperRepository {

    private ShopperSpringRepository shopperSpringRepository;

    public RdbmsShopperRepository(ShopperSpringRepository shopperSpringRepository) {
        this.shopperSpringRepository = shopperSpringRepository;
    }

    @Override
    public Shopper load(ShopperId id) {
        ShopperEntity shopperEntity = shopperSpringRepository.findById(id.value).get();
        return new Shopper(id, new FullName(shopperEntity.getName()));
    }
}
