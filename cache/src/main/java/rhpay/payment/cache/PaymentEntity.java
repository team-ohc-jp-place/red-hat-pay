package rhpay.payment.cache;

import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

public class PaymentEntity {

    private final int storeId;
    private final int billingAmount;
    private final long billingDateTime;

    @ProtoFactory
    public PaymentEntity(int storeId, int billingAmount, long billingDateTime) {
        this.storeId = storeId;
        this.billingAmount = billingAmount;
        this.billingDateTime = billingDateTime;
    }

    @ProtoField(number = 1, defaultValue = "0")
    public int getStoreId() {
        return storeId;
    }

    @ProtoField(number = 2, defaultValue = "0")
    public int getBillingAmount() {
        return billingAmount;
    }

    @ProtoField(number = 3, defaultValue = "0")
    public long getBillingDateTime() {
        return billingDateTime;
    }

    @Override
    public String toString() {
        return "PaymentEntity{" +
                "storeId=" + storeId +
                ", billingAmount=" + billingAmount +
                ", billingDateTime=" + billingDateTime +
                '}';
    }
}
