package rhpay.payment.repository.function;

import jdk.jfr.Event;
import org.infinispan.protostream.annotations.ProtoFactory;
import rhpay.monitoring.TokenFunctionEvent;
import rhpay.payment.cache.TokenEntity;
import rhpay.payment.cache.TokenKey;
import rhpay.payment.cache.TokenStatus;

import java.util.function.BiFunction;

/**
 * 処理中のトークンの状態を使用済みにする処理
 */
public class UsedTokenFunction implements BiFunction<TokenKey, TokenEntity, TokenEntity> {

    @ProtoFactory
    public UsedTokenFunction() {
        super();
    }

    @Override
    public TokenEntity apply(TokenKey tokenKey, TokenEntity cachedEntity) {
        Event functionEvent = new TokenFunctionEvent(UsedTokenFunction.class.getSimpleName(), tokenKey.getOwnerId(), tokenKey.getTokenId());
        functionEvent.begin();
        try {
            if (cachedEntity == null) {
                throw new RuntimeException(String.format("his token is not exist : %s", tokenKey.toString()));
            }
            if (!cachedEntity.getStatus().equals(TokenStatus.PROCESSING)) {
                throw new RuntimeException(String.format("Attempted to change status of cached tokens to 'used' even though it is '%s' : %s", cachedEntity.getStatus().getName(), tokenKey.toString()));
            }
            return new TokenEntity(TokenStatus.USED);
        } finally {
            functionEvent.commit();
        }
    }
}


