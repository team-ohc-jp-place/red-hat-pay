package rhpay.payment.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MoneyTest {

    @Test
    @DisplayName("金額を作成")
    public void createMoneyTest() {
        Money positiveMoney = new Money(1);
        assertEquals(1, positiveMoney.value);

        Money zeroMoney = new Money(0);
        assertEquals(0, zeroMoney.value);

        try {
            Money negativeMoney = new Money(-1);
            fail("マイナスの金額が作れてしまいました");
        } catch (Exception e) {
        }
    }

    @Test
    @DisplayName("金額の加算")
    public void addMoneyTest() {
        Money m1 = new Money(1);
        Money m2 = new Money(2);

        assertEquals(3, m1.add(m2).value);
    }

    @Test
    @DisplayName("金額の減算")
    public void minusMoneyTest() {
        Money m1 = new Money(1);
        Money m2 = new Money(2);

        assertEquals(1, m2.minus(m1).value);
        try{
            m1.minus(m2);
            fail("演算の結果、金額がマイナスになりました");
        }catch (Exception e){
        }
    }
}
