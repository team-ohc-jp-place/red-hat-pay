package rhpay.payment.domain;

public class TokenId {

    public final String value;

    public TokenId(String tokenId) {
        this.value = tokenId;
    }

    @Override
    public String toString() {
        return "TokenId{" +
                "value='" + value + '\'' +
                '}';
    }
}
