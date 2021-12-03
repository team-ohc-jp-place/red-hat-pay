package rhpay.monolith.repository.spring;

import org.springframework.data.repository.CrudRepository;
import rhpay.monolith.entity.PointEntity;

public interface PointSpringRepository extends CrudRepository<PointEntity, Integer> {
}
