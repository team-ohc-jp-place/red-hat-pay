package rhpay.payment.repository;

import rhpay.payment.domain.CoffeeStore;
import rhpay.payment.domain.StoreId;
import rhpay.payment.domain.StoreName;

public class MockCoffeeStoreRepository implements CoffeeStoreRepository{
    @Override
    public CoffeeStore load(StoreId storeId) {
        return new CoffeeStore(storeId, new StoreName("ABC Store"));
    }
}
