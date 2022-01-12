package rhpay.payment.repository;

import jdk.jfr.Event;
import org.infinispan.AdvancedCache;
import rhpay.monitoring.SegmentService;
import rhpay.monitoring.event.UseShopperEvent;
import rhpay.payment.cache.ShopperKey;
import rhpay.payment.cache.WalletEntity;
import rhpay.payment.domain.Money;
import rhpay.payment.domain.Shopper;
import rhpay.payment.domain.Wallet;

public class CacheWalletRepository implements WalletRepository{

    private final AdvancedCache<ShopperKey, WalletEntity> walletCache;

    public CacheWalletRepository(AdvancedCache<ShopperKey, WalletEntity> walletCache) {
        this.walletCache = walletCache;
    }

    @Override
    public Wallet load(Shopper shopper) {

        // 財布の所有者のキーを作成
        final ShopperKey shopperKey = new ShopperKey(shopper.getId().value);

        // 書込み用のロックを取りつつ取得する
        Event loadEvent = new UseShopperEvent("loadWallet", shopperKey.getOwnerId(), SegmentService.getSegment(walletCache, shopperKey));
        loadEvent.begin();

        WalletEntity cachedWallet = null;
        try {
            cachedWallet = walletCache.get(shopperKey);
        }finally {
            loadEvent.commit();
        }

        return new Wallet(shopper, new Money(cachedWallet.getChargedMoney()), new Money(cachedWallet.getAutoChargeMoney()));
    }

    @Override
    public void store(Wallet wallet) {

        // 財布の所有者のキーを作成
        final ShopperKey shopperKey = new ShopperKey(wallet.getOwner().getId().value);

        Event storeEvent = new UseShopperEvent("storeWallet", shopperKey.getOwnerId(), SegmentService.getSegment(walletCache, shopperKey));
        storeEvent.begin();

        try {
            walletCache.put(shopperKey, new WalletEntity(wallet.getChargedMoney().value, wallet.getAutoChargeMoney().value));
        }finally {
            storeEvent.commit();
        }
    }
}
