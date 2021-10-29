package rhpay.payment.cache;

import org.infinispan.distribution.group.Group;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

import java.util.Objects;

public class ShopperKey {
    private final int ownerId;

    @ProtoFactory
    public ShopperKey(int ownerId) {
        this.ownerId = ownerId;
    }

    @ProtoField(number = 1, defaultValue = "0")
    public int getOwnerId() {
        return ownerId;
    }

    @Group
    public String affinity() {
        return String.valueOf(ownerId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShopperKey shopperKey = (ShopperKey) o;
        return ownerId == shopperKey.ownerId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ownerId);
    }

    @Override
    public String toString() {
        return "ShopperKey{" +
                "ownerId=" + ownerId +
                '}';
    }
}
