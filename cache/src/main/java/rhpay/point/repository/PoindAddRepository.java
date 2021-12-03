package rhpay.point.repository;

import rhpay.payment.domain.Money;
import rhpay.payment.domain.ShopperId;
import rhpay.point.domain.Point;

public interface PoindAddRepository {
    Point addPoint(ShopperId shopperId, Money paidAmount);
}
