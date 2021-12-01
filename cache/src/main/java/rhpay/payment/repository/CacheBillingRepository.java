package rhpay.payment.repository;

import org.infinispan.lock.EmbeddedClusteredLockManagerFactory;
import org.infinispan.lock.api.ClusteredLock;
import org.infinispan.lock.api.ClusteredLockManager;
import rhpay.payment.cache.*;
import org.infinispan.Cache;
import rhpay.payment.cache.TokenStatus;
import rhpay.payment.domain.*;
import rhpay.payment.repository.function.*;

import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

public class CacheBillingRepository implements BillingRepository {

    private static final String LOCK_NAME = "lock";

    private final Cache<TokenKey, TokenEntity> tokenCache;
    private final Cache<ShopperKey, WalletEntity> walletCache;
    private final Cache<TokenKey, PaymentEntity> paymentCache;

    public CacheBillingRepository(Cache<TokenKey, TokenEntity> tokenCache, Cache<ShopperKey, WalletEntity> walletCache, Cache<TokenKey, PaymentEntity> paymentCache) {
        this.tokenCache = tokenCache;
        this.walletCache = walletCache;
        this.paymentCache = paymentCache;
    }

    private Payment payment;

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Payment bill(ShopperId shopperId, TokenId tokenId, Billing bill) throws PaymentException {

        final TokenKey tokenKey = new TokenKey(shopperId.value, tokenId.value);
        final ShopperKey shopperKey = new ShopperKey(shopperId.value);
        ClusteredLockManager clusteredLockManager = EmbeddedClusteredLockManagerFactory.from(walletCache.getCacheManager());
        if (!clusteredLockManager.isDefined(LOCK_NAME)) {
            clusteredLockManager.defineLock(LOCK_NAME);
        }
        ClusteredLock clusteredLock = clusteredLockManager.get(LOCK_NAME);

        try {
            // 指定されたトークンを処理中にする
            tokenCache.compute(tokenKey, new ProcessingTokenFunction());

            // 財布からお金を支払う
            clusteredLock.tryLock(1, TimeUnit.SECONDS).whenComplete((ret, exception) -> {
                if (ret) {
                    // ロックが取れたら整合性を必要とする処理をする
                    try {
                        WalletEntity cachedWallet = walletCache.get(shopperKey);
                        Shopper owner = new Shopper(new ShopperId(shopperKey.getOwnerId()), new FullName(""));
                        Wallet wallet = new Wallet(owner, new Money(cachedWallet.getChargedMoney()), new Money(cachedWallet.getAutoChargeMoney()));

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

            // トークンを使用済みにする
            tokenCache.compute(tokenKey, new UsedTokenFunction());

            PaymentEntity paymentEntity = new PaymentEntity(payment.getStoreId().value, payment.getBillingAmount().value, payment.getBillingDateTime().toEpochSecond(ZoneOffset.of("+09:00")));
            paymentCache.put(tokenKey, paymentEntity);

            return payment;

        } catch (Exception e) {
            // 例外が出たらトークンを失敗にする
            PaymentException thrw = new PaymentException("Fail to pay");
            try {

                TokenEntity tokenEntity = tokenCache.get(tokenKey);
                if (tokenEntity == null) {
                    thrw = new PaymentException(String.format("This token is not exist : %s", tokenKey));
                } else if (tokenEntity.getStatus().equals(TokenStatus.USED)) {
                    thrw = new PaymentException(String.format("Attempted to change status of cached tokens to 'failed' even though it is 'used' : %s", tokenKey));
                }

                tokenCache.compute(tokenKey, new FailedTokenFunction());
            } catch (Exception e1) {
                // 例外処理で例外が出たメッセージを出す
                thrw = new PaymentException("Exception is occurred again in catch statement");
                e.addSuppressed(e1);
                throw thrw;
            } finally {
                thrw.addSuppressed(e);
            }
            thrw.printStackTrace();
            throw thrw;
        }
    }
}