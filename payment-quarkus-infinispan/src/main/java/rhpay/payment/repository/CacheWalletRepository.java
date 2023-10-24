package rhpay.payment.repository;

import io.quarkus.infinispan.client.Remote;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.infinispan.client.hotrod.RemoteCache;
import rhpay.payment.cache.ShopperKey;
import rhpay.payment.cache.WalletEntity;
import rhpay.payment.domain.Money;
import rhpay.payment.domain.Shopper;
import rhpay.payment.domain.Wallet;

@RequestScoped
public class CacheWalletRepository implements WalletRepository {

    @Inject
    @Remote("wallet")
    RemoteCache<ShopperKey, WalletEntity> walletCache;

    public Wallet load(Shopper owner) {
        WalletEntity walletEntity = walletCache.get(new ShopperKey(owner.getId().value));
        Wallet wallet = new Wallet(owner, new Money(walletEntity.getChargedMoney()), new Money(walletEntity.getAutoChargeMoney()));

        return wallet;
    }

    public void store(Wallet wallet) {
        throw new UnsupportedOperationException("");
    }

}
