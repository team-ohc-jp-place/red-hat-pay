package rhpay.monolith.repository;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import rhpay.monolith.entity.PointEntity;
import rhpay.monolith.repository.spring.PointSpringRepository;
import rhpay.payment.domain.Payment;
import rhpay.payment.domain.ShopperId;
import rhpay.point.domain.Point;
import rhpay.point.repository.PointRepository;

@Component
@RequestScope
public class RdbmsPointRepository implements PointRepository {

    PointSpringRepository pointSpringRepository;

    public RdbmsPointRepository(PointSpringRepository pointSpringRepository) {
        this.pointSpringRepository = pointSpringRepository;
    }

    @Override
    public Point load(ShopperId shopperId) {
        PointEntity entity = pointSpringRepository.findById(shopperId.value).get();
        return new Point(new ShopperId(entity.getOwnerId()), entity.getAmount());
    }

    @Override
    public void store(Point point) {
        PointEntity entity = new PointEntity(point.getOwnerId().value, point.getPoint());
        pointSpringRepository.save(entity);
    }
}
