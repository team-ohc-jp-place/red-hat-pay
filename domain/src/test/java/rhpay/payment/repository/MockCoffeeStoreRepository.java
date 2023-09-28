package rhpay.payment.repository;

import rhpay.payment.domain.CoffeeStore;
import rhpay.payment.domain.StoreId;

public class MockCoffeeStoreRepository implements CoffeeStoreRepository{
    @Override
    public CoffeeStore load(StoreId storeId) {
        return null;
    }
}
