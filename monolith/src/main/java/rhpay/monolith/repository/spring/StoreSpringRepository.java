package rhpay.monolith.repository.spring;

import org.springframework.data.repository.CrudRepository;
import rhpay.monolith.entity.StoreEntity;

public interface StoreSpringRepository extends CrudRepository<StoreEntity, Integer> {
}
