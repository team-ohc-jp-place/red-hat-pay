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

    public Payment pay(Billing bill) throws FailedPaymentException {

        if (this.autoChargeable()) {
            // オートチャージされる場合
            try {
                while (!this.canPay(bill)) {
                    this.autoCharge();
                }
            }catch (FailedAutoChargeException e){
                FailedPaymentException newe = new FailedPaymentException(String.format("Couldn't buy items. Amount in wallet is %d, but store bill %d", this.chargedMoney.value, bill.getAmount().value));
                newe.addSuppressed(e);
                throw newe;
            }
        } else {
            // オートチャージされない場合
            if (!this.canPay(bill)) {
                throw new FailedPaymentException(String.format("Couldn't buy items. Amount in wallet is %d, but store bill %d", this.chargedMoney.value, bill.getAmount().value));
            }
        }

        Money newAmount = chargedMoney.minus(bill.getAmount());

        chargedMoney = newAmount;

        return new Payment(bill.getStoreId(), this.getOwner().getId(), bill.getAmount(), LocalDateTime.now());
    }

    public Money autoCharge() throws FailedAutoChargeException {
        if(!autoChargeable()){
            throw new FailedAutoChargeException("Could not auto charge");
        }
        chargedMoney = chargedMoney.add(autoChargeMoney);
        return chargedMoney;
    }

    public boolean autoChargeable() {
        return autoChargeMoney.isPositive();
    }

    public boolean canPay(Billing bill) {
        return chargedMoney.largerEqual(bill.getAmount());
    }
}
