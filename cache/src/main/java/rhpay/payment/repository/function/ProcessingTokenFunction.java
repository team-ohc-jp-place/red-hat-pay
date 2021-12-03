package rhpay.payment.repository.function;

import jdk.jfr.Event;
import org.infinispan.protostream.annotations.ProtoFactory;
import rhpay.monitoring.TokenFunctionEvent;
import rhpay.payment.cache.TokenEntity;
import rhpay.payment.cache.TokenKey;
import rhpay.payment.cache.TokenStatus;

import java.util.function.BiFunction;

/*
 * 未使用のトークンの状態を処理中にする処理
 */
public class ProcessingTokenFunction implements BiFunction<TokenKey, TokenEntity, TokenEntity> {

    @ProtoFactory
    public ProcessingTokenFunction() {
        super();
    }

    @Override
    public TokenEntity apply(TokenKey tokenKey, TokenEntity cachedEntity) {
        Event functionEvent = new TokenFunctionEvent(ProcessingTokenFunction.class.getSimpleName(), tokenKey.getOwnerId(), tokenKey.getTokenId());
        functionEvent.begin();
        try {
            if (cachedEntity == null) {
                throw new RuntimeException(String.format("This token is not exist : %s", tokenKey.toString()));
            }
            if (!cachedEntity.getStatus().equals(TokenStatus.UNUSED)) {
                throw new RuntimeException(String.format("Attempted to change status of cached tokens to 'processing' even though it is '%s' : %s", cachedEntity.getStatus().getName(), tokenKey.toString()));
            }
            return new TokenEntity(TokenStatus.PROCESSING);
        } finally {
            functionEvent.commit();
        }
    }
}