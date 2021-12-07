package rhpay.monolith.repository.spring;

import org.springframework.data.jpa.repository.JpaRepository;
import rhpay.monolith.entity.ShopperEntity;

public interface ShopperEntityRepository extends JpaRepository<ShopperEntity, Integer> {
}
