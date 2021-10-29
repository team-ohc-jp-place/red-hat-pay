package rhpay.payment.service;

import rhpay.payment.domain.CoffeeStore;
import rhpay.payment.domain.StoreId;
import rhpay.payment.repository.CoffeeStoreRepository;

public class CoffeeStoreService {

    private final CoffeeStoreRepository coffeeStoreRepository;

    public CoffeeStoreService(CoffeeStoreRepository coffeeStoreRepository) {
        this.coffeeStoreRepository = coffeeStoreRepository;
    }

    public CoffeeStore load(StoreId storeId){
        return coffeeStoreRepository.load(storeId);
    }
}
