package rhpay.payment.rest;

import java.time.LocalDateTime;

public class PaymentResponse {

    private int storeId;
    private int shopperId;

    private String tokenId;
    private int billingAmount;
    private LocalDateTime billingDateTime;

    public PaymentResponse() {
    }

    public PaymentResponse(int storeId, int shopperId, String tokenId, int billingAmount, LocalDateTime billingDateTime) {
        this.storeId = storeId;
        this.shopperId = shopperId;
        this.tokenId = tokenId;
        this.billingAmount = billingAmount;
        this.billingDateTime = billingDateTime;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public int getShopperId() {
        return shopperId;
    }

    public void setShopperId(int shopperId) {
        this.shopperId = shopperId;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public int getBillingAmount() {
        return billingAmount;
    }

    public void setBillingAmount(int billingAmount) {
        this.billingAmount = billingAmount;
    }

    public LocalDateTime getBillingDateTime() {
        return billingDateTime;
    }

    public void setBillingDateTime(LocalDateTime billingDateTime) {
        this.billingDateTime = billingDateTime;
    }
}
