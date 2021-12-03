package rhpay.monolith.repository.spring;

import org.springframework.data.repository.CrudRepository;
import rhpay.monolith.entity.WalletEntity;

public interface WalletSpringRepository extends CrudRepository<WalletEntity, Integer> {
}
