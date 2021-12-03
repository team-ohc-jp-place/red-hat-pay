package rhpay.payment.repository;

import rhpay.payment.domain.*;

public interface TokenRepository {
    void create(Token token);

    Token load(ShopperId shopperId, TokenId tokenId) throws TokenException;

    Token processing(Token token) throws TokenException;

    Token used(Token token) throws TokenException;

    Token failed(Token token) throws TokenException;
}
