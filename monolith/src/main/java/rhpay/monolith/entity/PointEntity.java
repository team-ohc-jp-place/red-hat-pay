package rhpay.monolith.entity;

import org.springframework.data.annotation.Id;

public class PointEntity {
    @Id
    private final int ownerId;
    private final int amount;

    public PointEntity(int ownerId, int amount) {
        this.ownerId = ownerId;
        this.amount = amount;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public int getAmount() {
        return amount;
    }
}
