package rhpay.payment.cache;

import org.infinispan.distribution.group.Group;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

import java.util.Objects;

public class TokenKey {

    private final int ownerId;
    private final String tokenId;

    @ProtoFactory
    public TokenKey(int ownerId, String tokenId) {
        this.ownerId = ownerId;
        this.tokenId = tokenId;
    }

    @ProtoField(number = 1, defaultValue = "0")
    public int getOwnerId() {
        return ownerId;
    }

    @ProtoField(number = 2)
    public String getTokenId() {
        return tokenId;
    }

    @Group
    public String affinity(){
        return String.valueOf(ownerId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenKey tokenKey = (TokenKey) o;
        return ownerId == tokenKey.ownerId && tokenId.equals(tokenKey.tokenId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ownerId, tokenId);
    }

    @Override
    public String toString() {
        return "TokenKey{" +
                "ownerId=" + ownerId +
                ", tokenId='" + tokenId + '\'' +
                '}';
    }
}
