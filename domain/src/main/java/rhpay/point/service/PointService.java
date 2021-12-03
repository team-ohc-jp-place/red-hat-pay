package rhpay.point.service;

import rhpay.payment.domain.Payment;
import rhpay.payment.domain.ShopperId;
import rhpay.point.domain.Point;
import rhpay.point.repository.PointRepository;

public class PointService {

    private final PointRepository pointRepository;

    public PointService(PointRepository pointRepository) {
        this.pointRepository = pointRepository;
    }

    public Point load(ShopperId shopperId){
        return pointRepository.load(shopperId);
    }

    public void store(Point point){
        pointRepository.store(point);
    }

}
