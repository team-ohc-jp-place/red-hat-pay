package rhpay.payment.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WalletTest {

    private static Money ZERO_MONEY = new Money(0);

    private static Shopper SHOPPER_1 = new Shopper(new ShopperId(1), new FullName("Shopper 1"));

    private static CoffeeStore STORE_1 = new CoffeeStore(new StoreId(1), new StoreName("Store 1"));

    @Test
    @DisplayName("オートチャージ額が0円に設定されているときにオートチャージできない")
    public void noAutoChargeTest() {
        Money chargedMoney = new Money(10);
        Wallet w = new Wallet(SHOPPER_1, chargedMoney, ZERO_MONEY);

        assertFalse(w.autoChargeable());

        try {
            w.autoCharge();
            fail();
        } catch (Exception e) {
            assertEquals("Could not auto charge", e.getMessage());
        }
    }

    @Test
    @DisplayName("オートチャージの金額が設定してあるとオートチャージできる")
    public void autoChargeTest() {
        Money chargedMoney = new Money(10);
        Wallet w1 = new Wallet(SHOPPER_1, chargedMoney, new Money(1));

        assertTrue(w1.autoChargeable());

        Money newChargedMoney = w1.autoCharge();
        assertEquals(11, newChargedMoney.value);
        assertEquals(11, w1.getChargedMoney().value);
    }

    @Test
    @DisplayName("財布に入っている金額内で何かを買う")
    public void payTest() {
        Money chargedMoney = new Money(10);
        Wallet w = new Wallet(SHOPPER_1, chargedMoney, ZERO_MONEY);

        Money billAmount = new Money(10);
        Billing billing = new Billing(STORE_1.getId(), billAmount);

        Payment payment = w.pay(billing);

        assertEquals(payment.getBillingAmount().value, 10);
        assertEquals(w.getChargedMoney().value, 0);
    }

    /**
     * For Workshop
     * テストコードのコメントを削除し、他のテストを参考に不足部分を追加してテストを完成させてください
     * 0 円を表すには ZERO_MONEY を使用してください。
     * ショップは SHOP_1 を使用してください
     * 買い物客は SHOPPER_1 を使用してください
     */
    @Test
    @DisplayName("残金が足りずオートチャージもないため支払いに失敗する")
    public void failToPayTest() {
/*
        // TODO: チャージされているお金を作成（5円）
        Money chargedMoney = ;
        // TODO: 請求される金額を作成（10円）
        Money billAmount = ;

        assertTrue(chargedMoney.value < billAmount.value);

        // TODO: 財布を作成。コンストラクタに必要な引数は、買い物客、チャージされている金額、オートチャージの金額　です
        Wallet wallet = ;
        // TODO: 請求を作成。コンストラクタに必要な引数は、ショップID、請求金額 です。
        Billing billing = ;

        try {
            // 支払いをします。しかし、残金が足りないために失敗します。
            Payment payment = wallet.pay(billing);

            // ここに来るとテストが失敗です。
            fail();
        } catch(Exception e){
            // 失敗のメッセージが正しいかを確認します。
            assertEquals("Couldn't buy items. Amount in wallet is 5, but store bill 10", e.getMessage());
        }
*/
    }

    /**
     * For Workshop
     * テストコードのコメントを削除し、他のテストを参考に TODO 部分を追加してテストを完成させてください
     * ショップは SHOP_1 を使用してください
     * 買い物客は SHOPPER_1 を使用してください
     */
    @Test
    @DisplayName("オートチャージにっよって支払いできる")
    public void payWithAutochargeTest() {
/*
        // TODO: チャージされているお金を chargedMoney として作成（10円）
        // TODO: オートチャージの金額を autoChargeMoney として作成（5円）
        // TODO: 請求される金額を billAmount として作成（11円）

        assertTrue(chargedMoney.value > billAmount.value);
        assertNotEquals(0, autoChargeMoney.value);

        // TODO: 財布を wallet として作成。コンストラクタに必要な引数は、買い物客、チャージされている金額、オートチャージの金額　です
        // TODO: 請求を billing として作成。コンストラクタに必要な引数は、ショップID、請求金額 です。

        try {
            // TODO: 財布と請求を使用して支払いをして、その戻り値のとなる支払い (Paymentクラス）を payment として作成
            // TODO: 財布の残高が 4 円であることを確認
        } catch (Exception e) {
            // ここに来るとテストが失敗です。
            fail();
        }
*/
    }
}
