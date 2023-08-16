package rhpay.payment.repository;

import rhpay.payment.domain.ShopperId;
import rhpay.payment.domain.Token;
import rhpay.payment.domain.TokenException;
import rhpay.payment.domain.TokenId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MockTokenRepository implements TokenRepository{

    private final List<Token> tokenData;

    public MockTokenRepository(List<Token> tokenData) {
        this.tokenData = tokenData;
    }

    public MockTokenRepository() {
        tokenData = new ArrayList<>();
    }

    @Override
    public void create(Token token) {
        tokenData.add(token);
    }

    @Override
    public Token load(ShopperId shopperId, TokenId tokenId) throws TokenException {
        return tokenData.stream().filter(t->t.getShopperId().equals(shopperId)).filter(t->t.getTokenId().equals(tokenId)).findFirst().get();
    }

    @Override
    public void store(Token token) throws TokenException {
        tokenData.add(token);
    }

    public List<Token> getCTokens() {
        return Collections.unmodifiableList(tokenData);
    }
}
