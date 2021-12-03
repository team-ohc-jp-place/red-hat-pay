package rhpay.point.service;

import rhpay.payment.domain.Payment;
import rhpay.point.domain.Point;
import rhpay.point.repository.PointDelegateRepository;

public class PointDelegateService {

    PointDelegateRepository pointDelegateRepository;

    public PointDelegateService(PointDelegateRepository pointDelegateRepository) {
        this.pointDelegateRepository = pointDelegateRepository;
    }

    public Point invoke(Payment payment){
        return pointDelegateRepository.invoke(payment);
    }
}
