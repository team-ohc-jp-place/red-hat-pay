package rhpay.payment.domain;

public class Money {
    public final int value;

    public Money(int value) {
        this.value = value;
    }

    public Money charge(Money chargeMoney) {
        return new Money(value + chargeMoney.value);
    }

    public boolean canPay(Money billingMoney) {
        return value > billingMoney.value;
    }

    public Money pay(Money billingMoney) {
        return new Money(value - billingMoney.value);
    }

    public boolean isBankrupt(){
        return value < 0;
    }

    @Override
    public String toString() {
        return "Money{" +
                "value=" + value +
                '}';
    }
}
