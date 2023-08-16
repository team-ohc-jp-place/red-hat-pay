package rhpay.payment.domain;

public class Money {
    public final int value;

    public Money(int value) {
        if(value < 0) {
            throw new RuntimeException("Negative value set to Money");
        }
        this.value = value;
    }

    public Money add(Money a) {
        return new Money(value + a.value);
    }

    public Money minus(Money a) {
        return new Money(value - a.value);
    }

    public boolean isNegative(){
        return value < 0;
    }

    public boolean isZero(){
        return value == 0;
    }

    public boolean isPositive(){
        return value > 0;
    }

    public boolean largerEqual(Money a){
        return value >= a.value;
    }

    @Override
    public String toString() {
        return "Money{" +
                "value=" + value +
                '}';
    }
}
