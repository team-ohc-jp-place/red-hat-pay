package rhpay.point.service;

import rhpay.payment.domain.Payment;
import rhpay.point.domain.Point;
import rhpay.point.repository.PointRepository;

public class PointService {

    private final PointRepository pointRepository;

    public PointService(PointRepository pointRepository) {
        this.pointRepository = pointRepository;
    }

    public Point givePoint(Payment payment) {
        return pointRepository.givePoint(payment);
    }

}
