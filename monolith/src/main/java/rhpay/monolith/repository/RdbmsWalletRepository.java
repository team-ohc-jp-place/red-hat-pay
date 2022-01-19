package rhpay.monolith.repository;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import rhpay.monolith.entity.WalletEntity;
import rhpay.monolith.repository.spring.WalletEntityRepository;
import rhpay.payment.domain.Money;
import rhpay.payment.domain.Shopper;
import rhpay.payment.domain.Wallet;
import rhpay.payment.repository.WalletRepository;

@Component
@RequestScope
public class RdbmsWalletRepository implements WalletRepository {

    WalletEntityRepository walletSpringRepository;

    public RdbmsWalletRepository(WalletEntityRepository walletSpringRepository) {
        this.walletSpringRepository = walletSpringRepository;
    }

    @Override
    public Wallet load(Shopper owner) {
        WalletEntity entity = walletSpringRepository.findByIdWithLock(owner.getId().value);
        return new Wallet(owner, new Money(entity.getChargedMoney()), new Money(entity.getAutoChargeMoney()));
    }

    @Override
    public void store(Wallet wallet) {
        int rowNum = walletSpringRepository.updateChargedMoney(wallet.getOwner().getId().value, wallet.getChargedMoney().value);
        if (rowNum == 0) {
            throw new RuntimeException(String.format("Wallet token is not exist on Data Store: %s", wallet));
        } else if (rowNum == 1) {
            return;
        }
        throw new RuntimeException(String.format("Many wallet were changed : %s", wallet));
    }
}
