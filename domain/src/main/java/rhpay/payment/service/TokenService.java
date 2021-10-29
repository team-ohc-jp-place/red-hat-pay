package rhpay.payment.service;

import rhpay.payment.repository.TokenRepository;
import rhpay.payment.domain.*;

import java.util.UUID;

public class TokenService {

    private final TokenRepository tokenRepository;

    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public Token create(ShopperId id){
        Token token = new Token(id, new TokenId(UUID.randomUUID().toString()), TokenStatus.UNUSED);
        tokenRepository.create(token);

        return token;
    }

    public Payment use(ShopperId shopperId, TokenId tokenId, CoffeeStore store, Money amount){
        return tokenRepository.use(shopperId, tokenId, store, amount);
    }

    public Token load(ShopperId shopperId, TokenId tokenId){
        return tokenRepository.load(shopperId, tokenId);
    }
}
