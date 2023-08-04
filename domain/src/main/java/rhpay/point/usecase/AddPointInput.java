package rhpay.point.usecase;

import rhpay.payment.domain.Payment;
import rhpay.point.repository.PointRepository;

public class AddPointInput {

    public AddPointInput(Payment payment, PointRepository repository) {
        this.payment = payment;
        this.repository = repository;
    }

    public final Payment payment;
    public final PointRepository repository;
}
