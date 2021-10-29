package rhpay.payment.event;

import java.time.LocalDateTime;

/**
 * 支払いのドメインイベント
 */
public class PaymentEvent {

    public LocalDateTime dateTime;
    public int userId;
    public int storeId;
    public String tokenId;
    public int amount;

    public PaymentEvent() {
    }

    public PaymentEvent(LocalDateTime dateTime, int userId, int storeId, String tokenId, int amount) {
        this.dateTime = dateTime;
        this.userId = userId;
        this.storeId = storeId;
        this.tokenId = tokenId;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "PaymentEvent{" +
                "dateTime=" + dateTime +
                ", userId=" + userId +
                ", storeId=" + storeId +
                ", tokenId='" + tokenId + '\'' +
                ", amount=" + amount +
                '}';
    }
}
