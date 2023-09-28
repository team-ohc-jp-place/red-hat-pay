package rhpay.point.usecase;

import rhpay.point.domain.Point;
import rhpay.point.service.PointService;

public class PointUsecase {

    public Point addPoint(AddPointInput input){

        PointService pointService = new PointService(input.repository);

        // ポイントを加算する
        Point point = pointService.load(input.payment.getShopperId());
        point.addPoint(input.payment.getBillingAmount());
        pointService.store(point);

        return point;
    }
}
