package rhpay.monolith.entity;

import org.springframework.data.annotation.Id;

public class StoreEntity {
    @Id
    private final int Id;
    private final String name;

    public StoreEntity(int id, String name) {
        Id = id;
        this.name = name;
    }

    public int getId() {
        return Id;
    }

    public String getName() {
        return name;
    }
}
