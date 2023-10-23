package rhpay.point.repository;

import rhpay.payment.domain.ShopperId;
import rhpay.point.domain.Point;

public interface PointRepository {

    Point load(ShopperId shopperId);

    void store(Point point);
}
