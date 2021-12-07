package rhpay.monolith.repository.spring;

import org.springframework.data.jpa.repository.JpaRepository;
import rhpay.monolith.entity.PointEntity;

public interface PointEntityRepository extends JpaRepository<PointEntity, Integer> {
}
