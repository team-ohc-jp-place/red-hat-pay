package rhpay.payment.domain;

import java.time.LocalDateTime;

public class Wallet {

    private Shopper owner;
    private Money chargedMoney;
    private Money autoChargeMoney;

    public Wallet(Shopper owner, Money chargedMoney, Money autoChargeMoney) {
        this.owner = owner;
        this.chargedMoney = chargedMoney;
        this.autoChargeMoney = autoChargeMoney;
    }

    public Shopper getOwner() {
        return owner;
    }

    public Money getChargedMoney() {
        return chargedMoney;
    }

    public Money getAutoChargeMoney() {
        return autoChargeMoney;
    }

    public Payment pay(Billing bill, TokenId tokenId) {

        if (autoChargeMoney.value > 0) {
            while (!chargedMoney.canPay(bill.getAmount())) {
                chargedMoney = chargedMoney.charge(autoChargeMoney);
            }
        }

        Money newAmount = chargedMoney.pay(bill.getAmount());

        if (newAmount.isBankrupt()) {
            throw new RuntimeException(String.format("Couldn't buy items. Amount in wallet is %d, but store bill %d", this.chargedMoney.value, bill.getAmount().value));
        }

        chargedMoney = newAmount;

        return new Payment(bill.getStoreId(), this.getOwner().getId(), tokenId, bill.getAmount(), LocalDateTime.now());
    }
}
