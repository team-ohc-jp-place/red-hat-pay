package rhpay.monolith.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Wallet")
@Cacheable(value = false)
public class WalletEntity implements Serializable {

    @Id
    @Column
    private int ownerId;

    @Column
    private int chargedMoney;

    @Column
    private int autoChargeMoney;

    public WalletEntity() {
    }

    public WalletEntity(int ownerId, int chargedMoney, int autoChargeMoney) {
        this.ownerId = ownerId;
        this.chargedMoney = chargedMoney;
        this.autoChargeMoney = autoChargeMoney;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public void setChargedMoney(int chargedMoney) {
        this.chargedMoney = chargedMoney;
    }

    public void setAutoChargeMoney(int autoChargeMoney) {
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
