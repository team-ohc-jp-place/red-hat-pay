package rhpay.point.domain;

import rhpay.payment.domain.Payment;
import rhpay.payment.domain.ShopperId;

public class Point {
    final ShopperId ownerId;
    final int point;

    public Point(ShopperId ownerId, int point) {
        this.ownerId = ownerId;
        this.point = point;
    }

    public ShopperId getOwnerId() {
        return ownerId;
    }

    public int getPoint() {
        return point;
    }

    private static final int RATE_PER_CENT = 1;

    public Point addPoint(Payment payment) {
        if (!ownerId.equals(payment.getShopperId())) {
            throw new RuntimeException("The owner and the shopper is not matched");
        }

        int addedPoint = payment.getBillingAmount().value * RATE_PER_CENT / 100;

        return new Point(this.ownerId, this.point + addedPoint);
    }

    @Override
    public String toString() {
        return "Point{" +
                "ownerId=" + ownerId +
                ", point=" + point +
                '}';
    }
}
