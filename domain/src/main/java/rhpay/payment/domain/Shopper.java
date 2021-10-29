package rhpay.payment.domain;

import java.util.Objects;

public class Shopper {

    private final ShopperId id;
    private final FullName name;

    public Shopper(ShopperId userId, FullName userName) {
        this.id = userId;
        this.name = userName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shopper shopper = (Shopper) o;
        return id.equals(shopper.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public ShopperId getId() {
        return id;
    }

    public FullName getUserName() {
        return name;
    }
}
