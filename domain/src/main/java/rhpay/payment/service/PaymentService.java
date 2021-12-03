package rhpay.payment.service;

import rhpay.payment.domain.Payment;
import rhpay.payment.domain.ShopperId;
import rhpay.payment.domain.TokenId;
import rhpay.payment.repository.PaymentRepository;

public class PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment load(ShopperId shopperId, TokenId tokenId) {
        return paymentRepository.load(shopperId, tokenId);
    }

    public void store(Payment payment){
        paymentRepository.store(payment);
    }
}
