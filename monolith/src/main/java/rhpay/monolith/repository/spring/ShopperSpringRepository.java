package rhpay.monolith.repository.spring;

import org.springframework.data.repository.CrudRepository;
import rhpay.monolith.entity.ShopperEntity;

public interface ShopperSpringRepository extends CrudRepository<ShopperEntity, Integer> {
}
