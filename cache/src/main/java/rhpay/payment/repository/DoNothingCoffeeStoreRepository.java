package rhpay.payment.repository;

import rhpay.payment.domain.CoffeeStore;
import rhpay.payment.domain.StoreId;

public class DoNothingCoffeeStoreRepository implements CoffeeStoreRepository{

    public DoNothingCoffeeStoreRepository(CoffeeStore store) {
        this.store = store;
    }

    private final CoffeeStore store;

    @Override
    public CoffeeStore load(StoreId storeId) {
        return this.store;
    }
}
