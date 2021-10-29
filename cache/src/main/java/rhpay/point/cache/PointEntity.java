package rhpay.point.cache;

import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

public class PointEntity {
    private final int amount;

    @ProtoFactory
    public PointEntity(int amount) {
        this.amount = amount;
    }

    @ProtoField(number = 1, defaultValue = "0")
    public int getAmount() {
        return amount;
    }
}
