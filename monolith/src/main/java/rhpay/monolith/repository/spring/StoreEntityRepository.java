package rhpay.monolith.repository.spring;

import org.springframework.data.jpa.repository.JpaRepository;
import rhpay.monolith.entity.StoreEntity;

public interface StoreEntityRepository extends JpaRepository<StoreEntity, Integer> {
}
