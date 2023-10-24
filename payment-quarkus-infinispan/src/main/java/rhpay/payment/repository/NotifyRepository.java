package rhpay.payment.repository;

import rhpay.payment.domain.Payment;

public interface NotifyRepository {

    void notify(Payment payment);
}
