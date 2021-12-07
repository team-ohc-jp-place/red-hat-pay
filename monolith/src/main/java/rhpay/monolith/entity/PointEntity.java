package rhpay.monolith.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "Point")
public class PointEntity implements Serializable {
    
    @Id
    private int ownerId;

    private int amount;

    public PointEntity() {
    }

    public PointEntity(int ownerId, int amount) {
        this.ownerId = ownerId;
        this.amount = amount;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public int getAmount() {
        return amount;
    }
}
