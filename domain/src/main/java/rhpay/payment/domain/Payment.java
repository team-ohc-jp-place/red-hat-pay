package rhpay.payment.domain;

import java.time.LocalDateTime;

public class Payment {

    private final StoreId storeId;
    private final ShopperId shopperId;

    private final Money billingAmount;
    private final LocalDateTime billingDateTime;

    public Payment(StoreId storeId, ShopperId shopperId, Money billingAmount, LocalDateTime billingDateTime) {
        this.storeId = storeId;
        this.shopperId = shopperId;
        this.billingAmount = billingAmount;
        this.billingDateTime = billingDateTime;
    }

    public StoreId getStoreId() {
        return storeId;
    }

    public ShopperId getShopperId() {
        return shopperId;
    }

    public Money getBillingAmount() {
        return billingAmount;
    }

    public LocalDateTime getBillingDateTime() {
        return billingDateTime;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "storeId=" + storeId +
                ", shopperId=" + shopperId +
                ", billingAmount=" + billingAmount +
                ", billingDateTime=" + billingDateTime +
                '}';
    }
}
