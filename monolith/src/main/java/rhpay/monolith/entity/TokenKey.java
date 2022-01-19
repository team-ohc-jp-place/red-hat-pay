package rhpay.monolith.entity;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TokenKey implements Serializable {

    private int ownerId;
    private String tokenId;

    public TokenKey() {
    }

    public TokenKey(int ownerId, String tokenId) {
        this.ownerId = ownerId;
        this.tokenId = tokenId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
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
}
