package rhpay.monolith.entity;

import org.springframework.data.annotation.Id;

public class TokenEntity {
    @Id
    private final int ownerId;
    @Id
    private final String tokenId;
    private final TokenStatus status;

    public TokenEntity(int ownerId, String tokenId, TokenStatus status) {
        this.ownerId = ownerId;
        this.tokenId = tokenId;
        this.status = status;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public String getTokenId() {
        return tokenId;
    }

    public TokenStatus getStatus() {
        return status;
    }
}
