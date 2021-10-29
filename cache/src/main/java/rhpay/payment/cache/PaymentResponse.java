package rhpay.payment.cache;

import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

import java.time.Instant;

public class PaymentResponse {

    private final int storeId;
    private final int shopperId;

    private final String tokenId;
    private final int pillingAmount;
    private final long billingDateTime;

    @ProtoFactory
    public PaymentResponse(int storeId, int shopperId, String tokenId, int pillingAmount, long billingDateTime) {
        this.storeId = storeId;
        this.shopperId = shopperId;
        this.tokenId = tokenId;
        this.pillingAmount = pillingAmount;
        this.billingDateTime = billingDateTime;
    }

    @ProtoField(number = 1, defaultValue = "0")
    public int getStoreId() {
        return storeId;
    }

    @ProtoField(number = 2, defaultValue = "0")
    public int getShopperId() {
        return shopperId;
    }

    @ProtoField(number = 3)
    public String getTokenId() {
        return tokenId;
    }

    @ProtoField(number = 4, defaultValue = "0")
    public int getPillingAmount() {
        return pillingAmount;
    }

    @ProtoField(number = 5, defaultValue = "0")
    public long getBillingDateTime() {
        return billingDateTime;
    }
}
