package rhpay.monolith.repository;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import rhpay.monolith.entity.PaymentEntity;
import rhpay.monolith.entity.TokenKey;
import rhpay.monolith.repository.spring.PaymentSpringRepository;
import rhpay.payment.domain.*;
import rhpay.payment.repository.PaymentRepository;

@Component
@RequestScope
public class RdbmsPaymentRepository implements PaymentRepository {

    PaymentSpringRepository paymentSpringRepository;

    public RdbmsPaymentRepository(PaymentSpringRepository paymentSpringRepository) {
        this.paymentSpringRepository = paymentSpringRepository;
    }

    @Override
    public Payment load(ShopperId shopperId, TokenId tokenId) {
        PaymentEntity entity = paymentSpringRepository.findById(new TokenKey(shopperId.value, tokenId.value)).get();
        return new Payment(new StoreId(entity.getStoreId()), shopperId, tokenId, new Money(entity.getBillingAmount()), entity.getBillingDateTime());
    }

    @Override
    public void store(Payment payment) {
        PaymentEntity entity = new PaymentEntity(payment.getShopperId().value, payment.getStoreId().value, payment.getBillingAmount().value, payment.getBillingDateTime());
    }
}
