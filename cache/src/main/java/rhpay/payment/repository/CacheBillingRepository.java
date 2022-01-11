package rhpay.payment.repository;

import jdk.jfr.Event;
import org.infinispan.AdvancedCache;
import rhpay.monitoring.SegmentService;
import rhpay.monitoring.event.UseShopperEvent;
import rhpay.payment.cache.ShopperKey;
import rhpay.payment.cache.WalletEntity;
import rhpay.payment.domain.*;

public class CacheBillingRepository implements BillingRepository {

    private final AdvancedCache<ShopperKey, WalletEntity> walletCache;

    public CacheBillingRepository(AdvancedCache<ShopperKey, WalletEntity> walletCache) {
        this.walletCache = walletCache;
    }

    public Payment bill(Shopper shopper, TokenId tokenId, Billing bill) throws PaymentException {

        // 財布の所有者のキーを作成
        final ShopperKey shopperKey = new ShopperKey(shopper.getId().value);

        // 書込み用のロックを取りつつ取得する
        Event loadEvent = new UseShopperEvent("loadWallet", shopperKey.getOwnerId(), SegmentService.getSegment(walletCache, shopperKey));
        loadEvent.begin();

        WalletEntity cachedWallet = null;
        try {
            cachedWallet = walletCache.get(shopperKey);
            if(cachedWallet == null){
                throw new PaymentException(String.format("Wallet is not cached [shopperId=%d]", shopperKey.getOwnerId()));
            }
        }catch(Exception e){
            throw new PaymentException(String.format("Could not load wallet [shopperId=%d]", shopperKey.getOwnerId()));
        }finally {
            loadEvent.commit();
        }

        // 業務処理を実行
        Wallet wallet = new Wallet(shopper, new Money(cachedWallet.getChargedMoney()), new Money(cachedWallet.getAutoChargeMoney()));
        Payment payment = wallet.pay(bill, tokenId);

        // 保存する
        Event storeEvent = new UseShopperEvent("storeWallet", shopperKey.getOwnerId(), SegmentService.getSegment(walletCache, shopperKey));
        storeEvent.begin();

        try {
            walletCache.put(shopperKey, new WalletEntity(wallet.getChargedMoney().value, wallet.getAutoChargeMoney().value));
        }catch(Exception e){
            throw new PaymentException(String.format("Could not store wallet [shopperId=%d]", shopperKey.getOwnerId()));
        }finally {
            storeEvent.commit();
        }

        return payment;
    }
}