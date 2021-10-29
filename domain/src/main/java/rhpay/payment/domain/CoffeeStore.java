package rhpay.payment.domain;

public class CoffeeStore {

    private final StoreId id;
    private final StoreName name;

    public CoffeeStore(StoreId id, StoreName name) {
        this.id = id;
        this.name = name;
    }

    public StoreId getId() {
        return id;
    }

    public StoreName getName() {
        return name;
    }

    public Billing createBill(Money amount){
        return new Billing(this.id, amount);
    }
}
