package rhpay.payment.domain;

public class Slip {

    private final Money billingAmount;
    private final StoreId storeId;

    public Slip(Money billingAmount, StoreId storeId) {
        this.billingAmount = billingAmount;
        this.storeId = storeId;
    }

    public Money getBillingAmount() {
        return billingAmount;
    }

    public StoreId getStoreId() {
        return storeId;
    }

    @Override
    public String toString() {
        return "Slip{" +
                "billingAmount=" + billingAmount +
                ", storeId=" + storeId +
                '}';
    }
}
