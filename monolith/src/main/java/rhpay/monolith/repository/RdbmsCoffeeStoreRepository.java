package rhpay.monolith.repository;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import rhpay.monolith.entity.StoreEntity;
import rhpay.monolith.repository.spring.StoreEntityRepository;
import rhpay.payment.domain.CoffeeStore;
import rhpay.payment.domain.StoreId;
import rhpay.payment.domain.StoreName;
import rhpay.payment.repository.CoffeeStoreRepository;

@Component
@RequestScope
public class RdbmsCoffeeStoreRepository implements CoffeeStoreRepository {

    StoreEntityRepository storeSpringRepository;

    public RdbmsCoffeeStoreRepository(StoreEntityRepository storeSpringRepository) {
        this.storeSpringRepository = storeSpringRepository;
    }

    @Override
    public CoffeeStore load(StoreId storeId) {
        StoreEntity entity = storeSpringRepository.findById(storeId.value).get();
        return new CoffeeStore(storeId, new StoreName(entity.getName()));
    }
}
