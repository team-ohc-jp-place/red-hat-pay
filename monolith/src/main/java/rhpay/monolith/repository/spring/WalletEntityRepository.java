package rhpay.monolith.repository.spring;

import org.springframework.data.jpa.repository.JpaRepository;
import rhpay.monolith.entity.WalletEntity;

public interface WalletEntityRepository extends JpaRepository<WalletEntity, Integer>, WalletEntityRepositoryCustom {
}
