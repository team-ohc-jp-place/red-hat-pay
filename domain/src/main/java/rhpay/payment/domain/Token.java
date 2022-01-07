package rhpay.payment.domain;

public class Token {

    private final ShopperId shopperId;
    private final TokenId tokenId;

    private final TokenStatus status;

    public Token(ShopperId shopperId, TokenId tokenId, TokenStatus status) {
        this.shopperId = shopperId;
        this.tokenId = tokenId;
        this.status = status;
    }

    public Token processing() throws TokenException {
        if (this.status.equals(TokenStatus.UNUSED)) {
            return new Token(this.shopperId, this.tokenId, TokenStatus.PROCESSING);
        } else {
            throw new TokenException(String.format("Attempted to change status of tokens to 'processing' even though it is '%s' : [%s, %s]", this.status.name, this.shopperId, this.tokenId));
        }
    }

    public Token used() throws TokenException {
        if (this.status.equals(TokenStatus.PROCESSING)) {
            return new Token(this.shopperId, this.tokenId, TokenStatus.USED);
        } else {
            throw new TokenException(String.format("Attempted to change status of tokens to 'used' even though it is '%s' : [%s, %s]", this.status.name, this.shopperId, this.tokenId));
        }
    }

    public Token failed() {
        return new Token(this.shopperId, this.tokenId, TokenStatus.FAILED);
    }


    public ShopperId getShopperId() {
        return shopperId;
    }

    public TokenId getTokenId() {
        return tokenId;
    }

    public TokenStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Token{" +
                "shopperId=" + shopperId +
                ", TokenId=" + tokenId +
                ", status=" + status +
                '}';
    }
}
