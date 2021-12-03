package rhpay.point;

import rhpay.payment.domain.Money;
import rhpay.payment.domain.ShopperId;
import rhpay.point.domain.Point;
import rhpay.point.repository.PoindAddRepository;

public class PointAddService {
    private final PoindAddRepository poindAddRepository;

    public PointAddService(PoindAddRepository poindAddRepository) {
        this.poindAddRepository = poindAddRepository;
    }

    public Point addPoint(ShopperId shopperId, Money paidAmount){
        return poindAddRepository.addPoint(shopperId, paidAmount);
    }
}
