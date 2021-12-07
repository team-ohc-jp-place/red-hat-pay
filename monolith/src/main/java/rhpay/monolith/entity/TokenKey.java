package rhpay.monolith.entity;

import javax.persistence.Embeddable;
import java.io.Serializable;

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
}
