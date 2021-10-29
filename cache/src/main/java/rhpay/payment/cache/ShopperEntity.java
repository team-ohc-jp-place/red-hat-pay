package rhpay.payment.cache;

import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

public class ShopperEntity {

    private final String name;

    @ProtoFactory
    public ShopperEntity(String name) {
        this.name = name;
    }


    @ProtoField(number = 1)
    public String getName() {
        return name;
    }
}
