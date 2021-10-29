package rhpay.payment.repository;

import rhpay.payment.domain.CoffeeStore;
import rhpay.payment.domain.StoreId;
import rhpay.payment.domain.StoreName;
import rhpay.monitoring.MonitorRepository;
import rhpay.payment.repository.CoffeeStoreRepository;

import javax.enterprise.context.RequestScoped;

@RequestScoped
@MonitorRepository
public class RdbmsCoffeeStoreRepository implements CoffeeStoreRepository {


    @Override
    public CoffeeStore load(StoreId storeId) {
        return new CoffeeStore(storeId, new StoreName("Demo Store"));
    }
}
