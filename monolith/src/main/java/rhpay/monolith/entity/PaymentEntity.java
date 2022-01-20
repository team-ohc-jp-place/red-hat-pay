package rhpay.monolith.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "Payment")
@Cacheable(value = false)
public class PaymentEntity implements Serializable {
    @EmbeddedId
    private TokenKey id;

    private int storeId;

    private int billingAmount;

    private LocalDateTime billingDateTime;

    public PaymentEntity() {
    }

    public PaymentEntity(TokenKey id, int storeId, int billingAmount, LocalDateTime billingDateTime) {
        this.id = id;
        this.storeId = storeId;
        this.billingAmount = billingAmount;
        this.billingDateTime = billingDateTime;
    }

    public TokenKey getId() {
        return id;
    }

    public void setId(TokenKey id) {
        this.id = id;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
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
