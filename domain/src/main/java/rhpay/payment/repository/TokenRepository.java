package rhpay.payment.repository;

import rhpay.payment.domain.*;

public interface TokenRepository {
    void create(Token token);

    Payment use(ShopperId shopperId, TokenId tokenId, CoffeeStore store, Money amount);

    Token load(ShopperId shopperId, TokenId tokenId);
}
