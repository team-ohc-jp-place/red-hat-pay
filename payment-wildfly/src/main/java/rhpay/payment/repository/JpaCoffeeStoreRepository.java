package rhpay.payment.repository;

import jakarta.enterprise.context.RequestScoped;
import rhpay.payment.domain.CoffeeStore;
import rhpay.payment.domain.StoreId;
import rhpay.payment.domain.StoreName;

//TODO: いつか作る
@RequestScoped
public class JpaCoffeeStoreRepository implements CoffeeStoreRepository{
    @Override
    public CoffeeStore load(StoreId storeId) {
        return new CoffeeStore(storeId, new StoreName("Demo Store"));
    }
}
