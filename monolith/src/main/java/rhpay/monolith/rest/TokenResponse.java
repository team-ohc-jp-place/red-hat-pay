package rhpay.monolith.rest;

import rhpay.payment.domain.TokenStatus;

public class TokenResponse {
    private int shopperId;
    private String TokenId;

    private TokenStatus status;

    public TokenResponse() {
    }

    public TokenResponse(int shopperId, String tokenId, TokenStatus status) {
        this.shopperId = shopperId;
        TokenId = tokenId;
        this.status = status;
    }

    public int getShopperId() {
        return shopperId;
    }

    public void setShopperId(int shopperId) {
        this.shopperId = shopperId;
    }

    public String getTokenId() {
        return TokenId;
    }

    public void setTokenId(String tokenId) {
        TokenId = tokenId;
    }

    public TokenStatus getStatus() {
        return status;
    }

    public void setStatus(TokenStatus status) {
        this.status = status;
    }
}
