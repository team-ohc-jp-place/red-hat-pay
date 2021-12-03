package rhpay.monolith.entity;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

public class PaymentEntity {
    @Id
    private final int ownerId;

    private final int storeId;
    private final int billingAmount;
    private final LocalDateTime billingDateTime;

    public PaymentEntity(int ownerId, int storeId, int billingAmount, LocalDateTime billingDateTime) {
        this.ownerId = ownerId;
        this.storeId = storeId;
        this.billingAmount = billingAmount;
        this.billingDateTime = billingDateTime;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public int getStoreId() {
        return storeId;
    }

    public int getBillingAmount() {
        return billingAmount;
    }

    public LocalDateTime getBillingDateTime() {
        return billingDateTime;
    }
}
