package rhpay.payment.domain;

import java.time.LocalDateTime;

public class Payment {

    private final StoreId storeId;
    private final ShopperId shopperId;

    private final TokenId tokenId;
    private final Money billingAmount;
    private final LocalDateTime billingDateTime;

    public Payment(StoreId storeId, ShopperId shopperId, TokenId tokenId, Money billingAmount, LocalDateTime billingDateTime) {
        this.storeId = storeId;
        this.shopperId = shopperId;
        this.tokenId = tokenId;
        this.billingAmount = billingAmount;
        this.billingDateTime = billingDateTime;
    }

    public StoreId getStoreId() {
        return storeId;
    }

    public ShopperId getShopperId() {
        return shopperId;
    }

    public Money getBillingAmount() {
        return billingAmount;
    }

    public LocalDateTime getBillingDateTime() {
        return billingDateTime;
    }

    public TokenId getTokenId() {
        return tokenId;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "storeId=" + storeId +
                ", shopperId=" + shopperId +
                ", tokenId=" + tokenId +
                ", billingAmount=" + billingAmount +
                ", billingDateTime=" + billingDateTime +
                '}';
    }
}
