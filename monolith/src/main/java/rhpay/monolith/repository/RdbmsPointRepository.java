package rhpay.monolith.repository;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import rhpay.monolith.entity.PointEntity;
import rhpay.monolith.repository.spring.PointEntityRepository;
import rhpay.payment.domain.ShopperId;
import rhpay.point.domain.Point;
import rhpay.point.repository.PointRepository;

@Component
@RequestScope
public class RdbmsPointRepository implements PointRepository {

    PointEntityRepository pointSpringRepository;

    public RdbmsPointRepository(PointEntityRepository pointSpringRepository) {
        this.pointSpringRepository = pointSpringRepository;
    }

    @Override
    public Point load(ShopperId shopperId) {
        PointEntity entity = pointSpringRepository.findById(shopperId.value).get();
        return new Point(new ShopperId(entity.getOwnerId()), entity.getAmount());
    }

    @Override
    public void store(Point point) {
        int rowNum = pointSpringRepository.updatePoint(point.getOwnerId().value, point.getPoint());
        if (rowNum == 0) {
            throw new RuntimeException(String.format("This point is not exist on Data Store: %s", point));
        } else if (rowNum == 1) {
            return;
        }
        throw new RuntimeException(String.format("Many point were changed : %s", point));
    }
}
