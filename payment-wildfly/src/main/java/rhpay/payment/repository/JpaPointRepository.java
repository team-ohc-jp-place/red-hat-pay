package rhpay.payment.repository;

import jakarta.enterprise.context.RequestScoped;
import rhpay.payment.domain.ShopperId;
import rhpay.point.domain.Point;
import rhpay.point.repository.PointRepository;

//TODO: いつか作る
@RequestScoped
public class JpaPointRepository implements PointRepository {
    @Override
    public Point load(ShopperId shopperId) {
        return new Point(shopperId, 1);
    }

    @Override
    public void store(Point point) {
        System.out.println(point);
    }
}
