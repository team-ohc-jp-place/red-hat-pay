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
        WalletEntity entity = new WalletEntity(wallet.getOwner().getId().value, wallet.getChargedMoney().value, wallet.getAutoChargeMoney().value);
        walletSpringRepository.save(entity);
    }
}
