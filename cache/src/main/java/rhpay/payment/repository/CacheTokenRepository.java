package rhpay.payment.repository;

import jdk.jfr.Event;
import org.infinispan.AdvancedCache;
import rhpay.monitoring.SegmentService;
import rhpay.monitoring.UseTokenEvent;
import rhpay.payment.cache.TokenEntity;
import rhpay.payment.cache.TokenKey;
import rhpay.payment.cache.TokenStatus;
import rhpay.payment.domain.ShopperId;
import rhpay.payment.domain.Token;
import rhpay.payment.domain.TokenException;
import rhpay.payment.domain.TokenId;

public class CacheTokenRepository implements TokenRepository {

    private final AdvancedCache<TokenKey, TokenEntity> tokenCache;

    public CacheTokenRepository(AdvancedCache<TokenKey, TokenEntity> tokenCache) {
        this.tokenCache = tokenCache;
    }

    @Override
    public void create(Token token) {
        throw new UnsupportedOperationException("token is created on application");
    }

    @Override
    public Token load(ShopperId shopperId, TokenId tokenId) throws TokenException {
        TokenKey key = new TokenKey(shopperId.value, tokenId.value);

        Event event = new UseTokenEvent("loadToken", shopperId.value, tokenId.value, SegmentService.getSegment(tokenCache, key));
        event.begin();

        try {
            TokenEntity tokenEntity = tokenCache.get(key);
            return new Token(shopperId, tokenId, tokenEntity.getStatus().toDomain());

        } catch (Exception e) {
            TokenException exception = new TokenException("Could not load token from cache");
            exception.addSuppressed(e);
            throw exception;

        } finally {
            event.commit();
        }
    }

    @Override
    public void store(Token token) throws TokenException {

        TokenKey key = new TokenKey(token.getShopperId().value, token.getTokenId().value);
        TokenEntity tokenEntity = new TokenEntity(TokenStatus.fromDomain(token.getStatus()));

        Event event = new UseTokenEvent("storeToken", token.getShopperId().value, token.getTokenId().value, SegmentService.getSegment(tokenCache, key));
        event.begin();

        try {
            tokenCache.put(key, tokenEntity);

        } catch (Exception e) {
            TokenException exception = new TokenException("Could not change the token's status");
            exception.addSuppressed(e);
            throw exception;

        } finally {
            event.commit();
        }
    }
}
