package rhpay.payment.domain;

public class Token {

    private final ShopperId shopperId;
    private final TokenId TokenId;

    private final TokenStatus status;

    public Token(ShopperId shopperId, TokenId tokenId, TokenStatus status) {
        this.shopperId = shopperId;
        TokenId = tokenId;
        this.status = status;
    }

    public ShopperId getShopperId() {
        return shopperId;
    }

    public TokenId getTokenId() {
        return TokenId;
    }

    public TokenStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Token{" +
                "shopperId=" + shopperId +
                ", TokenId=" + TokenId +
                ", status=" + status +
                '}';
    }
}
