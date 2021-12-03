package rhpay.payment.repository;

import rhpay.payment.cache.PaymentResponse;
import rhpay.payment.cache.TokenEntity;
import rhpay.payment.cache.TokenKey;
import rhpay.payment.cache.TokenStatus;
import rhpay.monitoring.MonitorRepository;
import io.quarkus.infinispan.client.Remote;
import org.infinispan.client.hotrod.RemoteCache;
import rhpay.payment.domain.*;
import rhpay.payment.repository.TokenRepository;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

@RequestScoped
@MonitorRepository
public class CacheTokenRepository implements TokenRepository {

    @Inject
    @Remote("token")
    RemoteCache<TokenKey, TokenEntity> tokenCache;

    @Override
    public void create(Token token) {

        TokenKey key = new TokenKey(token.getShopperId().value, token.getTokenId().value);
        tokenCache.put(key, new TokenEntity(TokenStatus.UNUSED));
    }

    @Override
    public Token load(ShopperId shopperId, TokenId tokenId) {
        TokenEntity tokenEntity = tokenCache.get(new TokenKey(shopperId.value, tokenId.value));
        return new Token(shopperId, tokenId, rhpay.payment.domain.TokenStatus.valueOf(tokenEntity.getStatus().toString()));
    }

    @Override
    public Token processing(Token token) {
        throw new UnsupportedOperationException("In this application, token is not used. Instead, it is delegated data grid.");
    }

    @Override
    public Token used(Token token) {
        throw new UnsupportedOperationException("In this application, token is not used. Instead, it is delegated data grid.");
    }

    @Override
    public Token failed(Token token) {
        throw new UnsupportedOperationException("In this application, token is not used. Instead, it is delegated data grid.");
    }


}
