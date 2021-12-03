package rhpay.payment.service;

import rhpay.payment.domain.*;
import rhpay.payment.repository.DelegateRepository;

public class DelegateService {

    DelegateRepository delegateRepository;

    public DelegateService(DelegateRepository delegateRepository) {
        this.delegateRepository = delegateRepository;
    }

    public Payment invoke(ShopperId shopperId, TokenId tokenId, CoffeeStore store, Money amount){
        return delegateRepository.invoke(shopperId, tokenId, store, amount);
    }

}
