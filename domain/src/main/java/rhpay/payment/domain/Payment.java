package rhpay.payment.domain;

import java.time.LocalDateTime;

public class Payment {

    private final StoreId storeId;
    private final ShopperId shopperId;

    private final Money billingAmount;
    private final LocalDateTime billingDateTime;

    private final TokenId tokenId;

    public Payment(StoreId storeId, ShopperId shopperId, Money billingAmount, LocalDateTime billingDateTime) {
        this.storeId = storeId;
        this.shopperId = shopperId;
        this.billingAmount = billingAmount;
        this.billingDateTime = billingDateTime;
        this.tokenId = null;
    }

    public Payment(StoreId storeId, ShopperId shopperId, Money billingAmount, LocalDateTime billingDateTime, TokenId tokenId) {
        this.storeId = storeId;
        this.shopperId = shopperId;
        this.billingAmount = billingAmount;
        this.billingDateTime = billingDateTime;
        this.tokenId = tokenId;
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
                ", billingAmount=" + billingAmount +
                ", billingDateTime=" + billingDateTime +
                ", tokenId=" + tokenId +
                '}';
    }
}
