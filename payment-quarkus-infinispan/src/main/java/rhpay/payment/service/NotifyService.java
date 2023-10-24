package rhpay.payment.service;

import rhpay.payment.domain.Payment;
import rhpay.payment.repository.NotifyRepository;

public class NotifyService {

    private final NotifyRepository paymentRepository;

    public NotifyService(NotifyRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public void notify(Payment payment) {
        paymentRepository.notify(payment);
    }
}