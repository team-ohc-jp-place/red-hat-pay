package rhpay.payment.repository;

import org.infinispan.lock.EmbeddedClusteredLockManagerFactory;
import org.infinispan.lock.api.ClusteredLock;
import org.infinispan.lock.api.ClusteredLockManager;
import rhpay.payment.cache.*;
import org.infinispan.Cache;
import rhpay.payment.domain.*;

import java.util.concurrent.TimeUnit;

public class CacheBillingRepository implements BillingRepository {

    private static final String LOCK_NAME = "lock";
    private static final long LOCK_TIMEOUT_SECONDS = 10;

    private final Cache<ShopperKey, WalletEntity> walletCache;

    public CacheBillingRepository(Cache<ShopperKey, WalletEntity> walletCache) {
        this.walletCache = walletCache;
    }

    private Payment payment;

    private void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Payment bill(Shopper shopper, TokenId tokenId, Billing bill) throws PaymentException {

        final ShopperKey shopperKey = new ShopperKey(shopper.getId().value);
        ClusteredLockManager clusteredLockManager = EmbeddedClusteredLockManagerFactory.from(walletCache.getCacheManager());
        if (!clusteredLockManager.isDefined(LOCK_NAME)) {
            clusteredLockManager.defineLock(LOCK_NAME);
        }
        ClusteredLock clusteredLock = clusteredLockManager.get(LOCK_NAME);

        try {
            // 財布からお金を支払う
            clusteredLock.tryLock(LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS).whenComplete((ret, exception) -> {
                if (ret) {
                    // ロックが取れたら整合性を必要とする処理をする
                    try {
                        WalletEntity cachedWallet = walletCache.get(shopperKey);
                        Wallet wallet = new Wallet(shopper, new Money(cachedWallet.getChargedMoney()), new Money(cachedWallet.getAutoChargeMoney()));

                        Payment payment = wallet.pay(bill, tokenId);
                        this.setPayment(payment);

                        walletCache.put(shopperKey, new WalletEntity(wallet.getChargedMoney().value, wallet.getAutoChargeMoney().value));

                    } finally {
                        // ロック期間の最後でアンロックする
                        clusteredLock.unlock();
                    }
                } else {
                    throw new RuntimeException("Fail to get the lock");
                }
            }).get();

            return payment;

        } catch (Exception e) {
            PaymentException paymentException = new PaymentException("Could not pay");
            paymentException.addSuppressed(e);
            throw paymentException;
        }
    }
}