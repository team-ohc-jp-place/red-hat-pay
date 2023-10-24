package rhpay.payment.repository;

import jakarta.enterprise.context.RequestScoped;
import rhpay.payment.domain.CoffeeStore;
import rhpay.payment.domain.StoreId;
import rhpay.payment.domain.StoreName;

@RequestScoped
public class RdbmsCoffeeStoreRepository implements CoffeeStoreRepository {


    @Override
    public CoffeeStore load(StoreId storeId) {
        return new CoffeeStore(storeId, new StoreName("Demo Store"));
    }
}
