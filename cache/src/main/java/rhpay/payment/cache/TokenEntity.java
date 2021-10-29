package rhpay.payment.cache;

import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

public class TokenEntity {

    private final TokenStatus status;

    @ProtoFactory
    public TokenEntity(TokenStatus status) {
        this.status = status;
    }

    @ProtoField(number = 1)
    public TokenStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "TokenEntity{" +
                "status=" + status +
                '}';
    }
}
