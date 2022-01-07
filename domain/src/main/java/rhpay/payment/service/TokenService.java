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

    public Token load(ShopperId shopperId, TokenId tokenId) throws TokenException{
        return tokenRepository.load(shopperId, tokenId);
    }

    public void store(Token token) throws TokenException{
        tokenRepository.store(token);
    }
}
