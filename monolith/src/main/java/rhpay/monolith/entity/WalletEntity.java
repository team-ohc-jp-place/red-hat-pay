package rhpay.monolith.entity;

import org.springframework.data.annotation.Id;

public class WalletEntity {
    @Id
    private final int ownerId;
    private final int chargedMoney;
    private final int autoChargeMoney;

    public WalletEntity(int ownerId, int chargedMoney, int autoChargeMoney) {
        this.ownerId = ownerId;
        this.chargedMoney = chargedMoney;
        this.autoChargeMoney = autoChargeMoney;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public int getChargedMoney() {
        return chargedMoney;
    }

    public int getAutoChargeMoney() {
        return autoChargeMoney;
    }
}
