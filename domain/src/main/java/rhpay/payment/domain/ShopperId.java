package rhpay.payment.domain;

import java.util.Objects;

public class ShopperId {
    public final int value;

    public ShopperId(int value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShopperId shopperId = (ShopperId) o;
        return value == shopperId.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "ShopperId{" +
                "value=" + value +
                '}';
    }
}
