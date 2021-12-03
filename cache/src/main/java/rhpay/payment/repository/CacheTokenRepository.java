package rhpay.payment.repository;

import org.infinispan.Cache;
import rhpay.payment.cache.TokenEntity;
import rhpay.payment.cache.TokenKey;
import rhpay.payment.domain.ShopperId;
import rhpay.payment.domain.Token;
import rhpay.payment.domain.TokenException;
import rhpay.payment.domain.TokenId;
import rhpay.payment.repository.function.FailedTokenFunction;
import rhpay.payment.repository.function.ProcessingTokenFunction;
import rhpay.payment.repository.function.UsedTokenFunction;

public class CacheTokenRepository implements TokenRepository {

    private final Cache<TokenKey, TokenEntity> tokenCache;

    public CacheTokenRepository(Cache<TokenKey, TokenEntity> tokenCache) {
        this.tokenCache = tokenCache;
    }

    @Override
    public void create(Token token) {
        throw new UnsupportedOperationException("token is created on application");
    }

    @Override
    public Token load(ShopperId shopperId, TokenId tokenId) throws TokenException {
        try {
            TokenEntity tokenEntity = tokenCache.get(new TokenKey(shopperId.value, tokenId.value));
            return new Token(shopperId, tokenId, tokenEntity.getStatus().toDomain());
        } catch (Exception e) {
            TokenException exception = new TokenException("Could not load token from cache");
            exception.addSuppressed(e);
            throw exception;
        }
    }

    @Override
    public Token processing(Token token) throws TokenException {
        try {
            TokenKey tokenKey = new TokenKey(token.getShopperId().value, token.getTokenId().value);
            TokenEntity tokenEntity = tokenCache.compute(tokenKey, new ProcessingTokenFunction());
            Token newToken = new Token(token.getShopperId(), token.getTokenId(), tokenEntity.getStatus().toDomain());
            return newToken;
        } catch (Exception e) {
            TokenException exception = new TokenException("Could not change the token's status");
            exception.addSuppressed(e);
            throw exception;
        }
    }

    @Override
    public Token used(Token token) throws TokenException {
        try {
            TokenKey tokenKey = new TokenKey(token.getShopperId().value, token.getTokenId().value);
            TokenEntity tokenEntity = tokenCache.compute(tokenKey, new UsedTokenFunction());
            Token newToken = new Token(token.getShopperId(), token.getTokenId(), tokenEntity.getStatus().toDomain());
            return newToken;
        } catch (Exception e) {
            TokenException exception = new TokenException("Could not change the token's status");
            exception.addSuppressed(e);
            throw exception;
        }

    }

    @Override
    public Token failed(Token token) throws TokenException {
        try {
            TokenKey tokenKey = new TokenKey(token.getShopperId().value, token.getTokenId().value);
            TokenEntity tokenEntity = tokenCache.compute(tokenKey, new FailedTokenFunction());
            Token newToken = new Token(token.getShopperId(), token.getTokenId(), tokenEntity.getStatus().toDomain());
            return newToken;
        } catch (Exception e) {
            TokenException exception = new TokenException("Could not change the token's status");
            exception.addSuppressed(e);
            throw exception;
        }

    }
}
