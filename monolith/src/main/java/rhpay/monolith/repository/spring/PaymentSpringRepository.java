package rhpay.monolith.repository.spring;

import org.springframework.data.repository.CrudRepository;
import rhpay.monolith.entity.PaymentEntity;
import rhpay.monolith.entity.TokenKey;

public interface PaymentSpringRepository extends CrudRepository<PaymentEntity, TokenKey> {
}
