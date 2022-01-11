package rhpay.point.repository.function;

import jdk.jfr.Event;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.util.function.SerializableBiFunction;
import rhpay.monitoring.event.ShopperFunctionEvent;
import rhpay.payment.cache.ShopperKey;
import rhpay.payment.domain.Money;
import rhpay.payment.domain.ShopperId;
import rhpay.point.cache.PointEntity;
import rhpay.point.domain.Point;

public class PointAddFunction implements SerializableBiFunction<ShopperKey, PointEntity, PointEntity> {

    private final int paidAmount;

    @ProtoFactory
    public PointAddFunction(int paidAmount) {
        this.paidAmount = paidAmount;
    }

    @ProtoField(number = 1, defaultValue = "0")
    public int getPaidAmount() {
        return paidAmount;
    }

    @Override
    public PointEntity apply(ShopperKey pointKey, PointEntity cachedPointData) {

        Event functionEvent = new ShopperFunctionEvent("PointAddFunction", pointKey.getOwnerId());
        functionEvent.begin();

        try {
            // キャッシュされたオブジェクトからドメインのオブジェクトを作成
            Point currentPoint = new Point(new ShopperId(pointKey.getOwnerId()), cachedPointData.getAmount());

            // 支払われた金額からポイントを加算する処理
            Point newPoint = currentPoint.addPoint(new Money(paidAmount));

            // レスポンスを返す
            return new PointEntity(newPoint.getPoint());
        } finally {
            functionEvent.commit();
        }
    }
}