package rhpay.payment.repository;

import rhpay.payment.domain.Payment;
import rhpay.payment.domain.ShopperId;
import rhpay.payment.domain.TokenId;

public interface PaymentRepository {
    Payment load(ShopperId shopperId, TokenId tokenId);
    void store(Payment payment);
}
