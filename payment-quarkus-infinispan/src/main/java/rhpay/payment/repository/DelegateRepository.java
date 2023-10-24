package rhpay.payment.repository;

import rhpay.payment.domain.*;

public interface DelegateRepository {
    public Payment invoke(ShopperId shopperId, TokenId tokenId, CoffeeStore store, Money amount);
}
