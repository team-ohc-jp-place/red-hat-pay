package rhpay.point.repository;

import rhpay.payment.domain.Payment;
import rhpay.point.domain.Point;

public interface PointDelegateRepository {
    Point invoke(Payment payment);
}
