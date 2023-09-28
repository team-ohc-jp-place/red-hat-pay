package rhpay.payment.domain;

public class Token {

    private final ShopperId shopperId;
    private final TokenId tokenId;

    private final TokenStatus status;

    private final Payment payment;

    public Token(ShopperId shopperId, TokenId tokenId, TokenStatus status) {
        this.shopperId = shopperId;
        this.tokenId = tokenId;
        this.status = status;
        this.payment = null;
    }

    public Token(ShopperId shopperId, TokenId tokenId, TokenStatus status, Payment payment) {
        this.shopperId = shopperId;
        this.tokenId = tokenId;
        this.status = status;
        this.payment = payment;
    }

    public Token processing() throws TokenException {
        if (this.status.equals(TokenStatus.UNUSED)) {
            return new Token(this.shopperId, this.tokenId, TokenStatus.PROCESSING);
        } else {
            throw new TokenException(String.format("Attempted to change status of tokens to 'processing' even though it is '%s' : [%s, %s]", this.status.name, this.shopperId, this.tokenId));
        }
    }

    public Token used(Payment payment) throws TokenException {
        if (this.status.equals(TokenStatus.PROCESSING)) {
            Payment relatedPayment = new Payment(payment.getStoreId(), payment.getShopperId(), payment.getBillingAmount(), payment.getBillingDateTime(), tokenId);
            return new Token(this.shopperId, this.tokenId, TokenStatus.USED, relatedPayment);
        } else {
            throw new TokenException(String.format("Attempted to change status of tokens to 'used' even though it is '%s' : [%s, %s]", this.status.name, this.shopperId, this.tokenId));
        }
    }

    public Payment getRelatedPayment(){
        return this.payment;
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
