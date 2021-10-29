package rhpay.payment.domain;

public class Billing {

    private final StoreId storeId;
    private final Money amount;

    public Billing(StoreId storeId, Money amount) {
        this.storeId = storeId;
        this.amount = amount;
    }

    public Money getAmount() {
        return this.amount;
    }

    public StoreId getStoreId() {
        return storeId;
    }
}
