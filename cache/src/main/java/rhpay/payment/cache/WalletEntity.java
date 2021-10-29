package rhpay.payment.cache;

import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

public class WalletEntity {

    private int chargedMoney;
    private int autoChargeMoney;

    @ProtoFactory
    public WalletEntity(int chargedMoney, int autoChargeMoney) {
        this.chargedMoney = chargedMoney;
        this.autoChargeMoney = autoChargeMoney;
    }

    @ProtoField(number = 1, defaultValue = "0")
    public int getChargedMoney() {
        return chargedMoney;
    }

    @ProtoField(number = 2, defaultValue = "0")
    public int getAutoChargeMoney() {
        return autoChargeMoney;
    }

    @Override
    public String toString() {
        return "WalletEntity{" +
                "chargedMoney=" + chargedMoney +
                ", autoChargeMoney=" + autoChargeMoney +
                '}';
    }
}
