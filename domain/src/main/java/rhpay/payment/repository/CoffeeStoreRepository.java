package rhpay.payment.repository;

import rhpay.payment.domain.CoffeeStore;
import rhpay.payment.domain.StoreId;

public interface CoffeeStoreRepository {
    CoffeeStore load(StoreId storeId);
}
