package rhpay.payment.repository;

import io.quarkus.infinispan.client.Remote;
import jdk.jfr.Event;
import org.infinispan.client.hotrod.RemoteCache;
import rhpay.monitoring.TokenRepositoryEvent;
import rhpay.monitoring.TracerService;
import rhpay.payment.cache.TokenEntity;
import rhpay.payment.cache.TokenKey;
import rhpay.payment.cache.TokenStatus;
import rhpay.payment.domain.ShopperId;
import rhpay.payment.domain.Token;
import rhpay.payment.domain.TokenId;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class CacheTokenRepository implements TokenRepository {

    @Inject
    @Remote("token")
    RemoteCache<TokenKey, TokenEntity> tokenCache;

    @Inject
    TracerService tracerService;

    @Override
    public void create(Token token) {
        String traceId = tracerService.traceRepository();

        Event event = new TokenRepositoryEvent(traceId, "notify", token.getShopperId().value, token.getTokenId().value);
        event.begin();

        try {

            TokenKey key = new TokenKey(token.getShopperId().value, token.getTokenId().value);
            tokenCache.put(key, new TokenEntity(TokenStatus.UNUSED));

        } finally {
            event.commit();
            tracerService.closeTrace();
        }
    }

    @Override
    public Token load(ShopperId shopperId, TokenId tokenId) {
        TokenEntity tokenEntity = tokenCache.get(new TokenKey(shopperId.value, tokenId.value));
        return new Token(shopperId, tokenId, rhpay.payment.domain.TokenStatus.valueOf(tokenEntity.getStatus().toString()));
    }

    @Override
    public void store(Token token) {
        TokenKey key = new TokenKey(token.getShopperId().value, token.getTokenId().value);
        tokenCache.put(key, new TokenEntity(TokenStatus.fromDomain(token.getStatus())));
    }
}
