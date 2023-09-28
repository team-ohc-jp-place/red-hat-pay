package rhpay.payment.repository;

import jakarta.enterprise.context.RequestScoped;
import rhpay.payment.domain.*;

//TODO: いつか作る
@RequestScoped
public class JpaTokenRepository implements TokenRepository{
    @Override
    public void create(Token token) {
        System.out.println(token);
    }

    @Override
    public Token load(ShopperId shopperId, TokenId tokenId) throws TokenException {
        return new Token(shopperId, tokenId, TokenStatus.UNUSED);
    }

    @Override
    public void store(Token token) throws TokenException {
        System.out.println(token);
    }
}
