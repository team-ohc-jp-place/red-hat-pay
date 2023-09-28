package rhpay.payment.repository;

import rhpay.payment.domain.Payment;
import rhpay.payment.domain.ShopperId;
import rhpay.payment.domain.TokenId;

public class MockPaymentRepository implements PaymentRepository{
    @Override
    public Payment load(ShopperId shopperId, TokenId tokenId) {
        return null;
    }

    @Override
    public void store(Payment payment) {

    }
}
