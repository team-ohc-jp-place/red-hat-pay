package rhpay.monolith.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "Shopper")
public class ShopperEntity implements Serializable {

    @Id
    private int ownerId;

    private String name;

    public ShopperEntity() {
    }

    public ShopperEntity(int ownerId, String name) {
        this.ownerId = ownerId;
        this.name = name;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public String getName() {
        return name;
    }
}