package rhpay.monolith.repository.spring;

import org.springframework.data.jpa.repository.JpaRepository;
import rhpay.monolith.entity.PaymentEntity;
import rhpay.monolith.entity.TokenKey;

public interface PaymentEntityRepository extends JpaRepository<PaymentEntity, TokenKey> {
}
