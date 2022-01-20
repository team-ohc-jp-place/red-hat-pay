package rhpay.monolith.repository;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import rhpay.monolith.entity.ShopperEntity;
import rhpay.monolith.repository.spring.ShopperEntityRepository;
import rhpay.payment.domain.FullName;
import rhpay.payment.domain.Shopper;
import rhpay.payment.domain.ShopperId;
import rhpay.payment.repository.ShopperRepository;

import java.util.NoSuchElementException;

@Component
@RequestScope
public class RdbmsShopperRepository implements ShopperRepository {

    private ShopperEntityRepository shopperSpringRepository;

    public RdbmsShopperRepository(ShopperEntityRepository shopperSpringRepository) {
        this.shopperSpringRepository = shopperSpringRepository;
    }

    @Override
    public Shopper load(ShopperId id) {
        try {
            ShopperEntity shopperEntity = shopperSpringRepository.findById(id.value).get();
            return new Shopper(id, new FullName(shopperEntity.getName()));
        }catch(NoSuchElementException e){
            e.addSuppressed(new RuntimeException(String.format("Target shopper is %s", id)));
            throw e;
        }
    }
}
