package rhpay.monolith.entity;

public class TokenKey {
    private final int ownerId;
    private final String tokenId;

    public TokenKey(int ownerId, String tokenId) {
        this.ownerId = ownerId;
        this.tokenId = tokenId;
    }
}
