package rhpay.payment.repository.function;

import jdk.jfr.Event;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.util.function.SerializableBiFunction;
import rhpay.monitoring.TokenFunctionEvent;
import rhpay.payment.cache.TokenEntity;
import rhpay.payment.cache.TokenKey;
import rhpay.payment.cache.TokenStatus;

/*
 * 処理中のトークンの状態を失敗にする処理
 */
public class FailedTokenFunction implements SerializableBiFunction<TokenKey, TokenEntity, TokenEntity> {

    @ProtoFactory
    public FailedTokenFunction() {
        super();
    }

    @Override
    public TokenEntity apply(TokenKey tokenKey, TokenEntity cachedEntity) {
        Event functionEvent = new TokenFunctionEvent(FailedTokenFunction.class.getSimpleName(), tokenKey.getOwnerId(), tokenKey.getTokenId());
        functionEvent.begin();
        try {
            return new TokenEntity(TokenStatus.FAILED);
        } finally {
            functionEvent.commit();
        }
    }
}
