package rhpay.payment.repository;

import rhpay.payment.cache.ShopperKey;
import rhpay.payment.cache.WalletEntity;
import rhpay.monitoring.MonitorRepository;
import io.quarkus.infinispan.client.Remote;
import org.infinispan.client.hotrod.RemoteCache;
import rhpay.payment.domain.*;
import rhpay.payment.repository.WalletRepository;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
@MonitorRepository
public class CacheWalletRepository implements WalletRepository {

    @Inject
    @Remote("wallet")
    RemoteCache<ShopperKey, WalletEntity> walletCache;

    public Wallet load(Shopper owner) {
        WalletEntity walletEntity = walletCache.get(new ShopperKey(owner.getId().value));
        Wallet wallet = new Wallet(owner, new Money(walletEntity.getChargedMoney()), new Money(walletEntity.getAutoChargeMoney()));

        return wallet;
    }

    public void store(Wallet wallet){
        throw new UnsupportedOperationException("");
    }

}
