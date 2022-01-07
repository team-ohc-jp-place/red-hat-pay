package rhpay.payment.repository;

import rhpay.payment.domain.*;

public interface TokenRepository {
    void create(Token token);

    Token load(ShopperId shopperId, TokenId tokenId) throws TokenException;

    void store(Token token) throws TokenException;
}
