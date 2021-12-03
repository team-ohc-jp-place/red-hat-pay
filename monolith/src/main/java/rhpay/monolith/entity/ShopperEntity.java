package rhpay.monolith.entity;

import org.springframework.data.annotation.Id;

public class ShopperEntity {

    @Id
    private final int ownerId;
    private final String name;

    public ShopperEntity(int ownerId, String name) {
        this.ownerId = ownerId;
        this.name = name;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public String getName() {
        return name;
    }
}