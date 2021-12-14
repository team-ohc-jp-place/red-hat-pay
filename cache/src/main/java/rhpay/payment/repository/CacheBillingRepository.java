package rhpay.payment.repository;

import jdk.jfr.Event;
import org.infinispan.Cache;
import org.infinispan.context.Flag;
import rhpay.monitoring.CacheUseEvent;
import rhpay.monitoring.SegmentService;
import rhpay.payment.cache.ShopperKey;
import rhpay.payment.cache.WalletEntity;
import rhpay.payment.domain.*;

public class CacheBillingRepository implements BillingRepository {

    private final Cache<ShopperKey, WalletEntity> walletCache;

    public CacheBillingRepository(Cache<ShopperKey, WalletEntity> walletCache) {
        this.walletCache = walletCache;
    }

    public Payment bill(Shopper shopper, TokenId tokenId, Billing bill) throws PaymentException {

        // 財布の所有者のキーを作成
        final ShopperKey shopperKey = new ShopperKey(shopper.getId().value);

        // 書込み用のロックを取りつつ取得する
        Event loadEvent = new CacheUseEvent(SegmentService.getSegment(walletCache, shopperKey), "loadWallet");
        loadEvent.begin();
        WalletEntity cachedWallet = walletCache.getAdvancedCache().withFlags(Flag.FORCE_WRITE_LOCK).get(shopperKey);
        loadEvent.commit();

        Wallet wallet = new Wallet(shopper, new Money(cachedWallet.getChargedMoney()), new Money(cachedWallet.getAutoChargeMoney()));
        Payment payment = wallet.pay(bill, tokenId);

        Event storeEvent = new CacheUseEvent(SegmentService.getSegment(walletCache, shopperKey), "storeWallet");
        storeEvent.begin();
        walletCache.put(shopperKey, new WalletEntity(wallet.getChargedMoney().value, wallet.getAutoChargeMoney().value));
        storeEvent.commit();

        return payment;
    }
}