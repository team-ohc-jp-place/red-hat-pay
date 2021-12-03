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

    public Token processing(Token token) throws TokenException{
        if(token.getStatus().equals(TokenStatus.UNUSED)) {
            return tokenRepository.processing(token);
        } else {
            throw new TokenException(String.format("Attempted to change status of tokens to 'processing' even though it is '%s' : [%s, %s]", token.getStatus().name, token.getShopperId(), token.getTokenId()));
        }
    }

    public Token used(Token token) throws TokenException{
        if(token.getStatus().equals(TokenStatus.PROCESSING)){
        return tokenRepository.used(token);
        } else {
            throw new TokenException(String.format("Attempted to change status of tokens to 'used' even though it is '%s' : [%s, %s]", token.getStatus().name, token.getShopperId(), token.getTokenId()));
        }
    }

    public Token failed(Token token) throws TokenException{
        return tokenRepository.failed(token);
    }

}
