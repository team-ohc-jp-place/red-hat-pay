package rhpay.payment.repository;

import rhpay.payment.cache.*;
import rhpay.monitoring.ShopperFunctionEvent;
import rhpay.monitoring.TokenFunctionEvent;
import jdk.jfr.Event;
import org.infinispan.Cache;
import org.infinispan.util.function.SerializableBiFunction;
import rhpay.payment.cache.TokenStatus;
import rhpay.payment.domain.*;

import java.time.ZoneOffset;

public class CacheBillingRepository implements BillingRepository {

    private final Cache<TokenKey, TokenEntity> tokenCache;
    private final Cache<ShopperKey, WalletEntity> walletCache;
    private final Cache<TokenKey, PaymentEntity> paymentCache;

    public CacheBillingRepository(Cache<TokenKey, TokenEntity> tokenCache, Cache<ShopperKey, WalletEntity> walletCache, Cache<TokenKey, PaymentEntity> paymentCache) {
        this.tokenCache = tokenCache;
        this.walletCache = walletCache;
        this.paymentCache = paymentCache;
    }

    public Payment bill(ShopperId shopperId, TokenId tokenId, Billing bill) {

        final TokenKey tokenKey = new TokenKey(shopperId.value, tokenId.value);

        try {
            // 指定されたトークンを処理中にする
            tokenCache.compute(tokenKey, new ProcessingTokenFunction());

            // 財布から請求額を出す
            final BillingFunction billingFunction = new BillingFunction(bill, tokenId);
            final ShopperKey shopperKey = new ShopperKey(shopperId.value);
            walletCache.compute(shopperKey, billingFunction);

            // 財布からお金を支払う
            Payment payment = billingFunction.getPayment();

            // トークンを使用済みにする
            tokenCache.compute(tokenKey, new UsedTokenFunction());

            paymentCache.put(tokenKey, new PaymentEntity(payment.getStoreId().value, payment.getBillingAmount().value, payment.getBillingDateTime().toEpochSecond(ZoneOffset.of("+09:00"))));

            return payment;

        } catch (Exception e) {
            // 例外が出たらトークンを失敗にする
            try {
                tokenCache.compute(tokenKey, new FailedTokenFunction(e));
            } catch (Exception e1) {
                throw e1;
            }
            throw e;
        }
    }
}

/**
 * 財布から請求された金額を支払う処理
 */
class BillingFunction implements SerializableBiFunction<ShopperKey, WalletEntity, WalletEntity> {

    private final Billing bill;
    private final TokenId tokenId;

    public BillingFunction(Billing bill, TokenId tokenId) {
        this.bill = bill;
        this.tokenId = tokenId;
    }

    private Payment payment = null;

    public Payment getPayment() {
        return payment;
    }

    @Override
    public WalletEntity apply(ShopperKey ownerKey, WalletEntity cachedWallet) {

        Event functionEvent = new ShopperFunctionEvent("BillingFunction", ownerKey.getOwnerId());
        functionEvent.begin();

        try {
            if (cachedWallet == null) {
                throw new RuntimeException(String.format("このユーザの財布は登録されていません %s", ownerKey));
            }

            Shopper owner = new Shopper(new ShopperId(ownerKey.getOwnerId()), new FullName(""));
            Wallet wallet = new Wallet(owner, new Money(cachedWallet.getChargedMoney()), new Money(cachedWallet.getAutoChargeMoney()));

            payment = wallet.pay(bill, tokenId);

            return new WalletEntity(wallet.getChargedMoney().value, wallet.getAutoChargeMoney().value);
        } finally {
            functionEvent.commit();
        }
    }
}

/**
 * 未使用のトークンの状態を処理中にする処理
 */
class ProcessingTokenFunction implements SerializableBiFunction<TokenKey, TokenEntity, TokenEntity> {

    @Override
    public TokenEntity apply(TokenKey tokenKey, TokenEntity cachedEntity) {
        Event functionEvent = new TokenFunctionEvent("ProcessingTokenFunction", tokenKey.getOwnerId(), tokenKey.getTokenId());
        functionEvent.begin();
        try {
            if (cachedEntity == null) {
                throw new RuntimeException(String.format("指定されたトークンは存在しません %s", tokenKey.toString()));
            }
            if (!cachedEntity.getStatus().equals(TokenStatus.UNUSED)) {
                throw new RuntimeException(String.format("キャッシュされているトークンのステータスが %s なのに 処理中 に変更しようとしました %s", cachedEntity.getStatus().getName(), tokenKey.toString()));
            }
            return new TokenEntity(TokenStatus.PROCESSING);
        } finally {
            functionEvent.commit();
        }
    }
}

/**
 * 処理中のトークンの状態を使用済みにする処理
 */
class UsedTokenFunction implements SerializableBiFunction<TokenKey, TokenEntity, TokenEntity> {

    @Override
    public TokenEntity apply(TokenKey tokenKey, TokenEntity cachedEntity) {
        Event functionEvent = new TokenFunctionEvent("ProcessingTokenFunction", tokenKey.getOwnerId(), tokenKey.getTokenId());
        functionEvent.begin();
        try {
            if (cachedEntity == null) {
                throw new RuntimeException(String.format("指定されたトークンは存在しません %s", tokenKey.toString()));
            }
            if (!cachedEntity.getStatus().equals(TokenStatus.PROCESSING)) {
                throw new RuntimeException(String.format("キャッシュされているトークンのステータスが %s なのに 使用済み に変更しようとしました %s", cachedEntity.getStatus().getName(), tokenKey.toString()));
            }
            return new TokenEntity(TokenStatus.USED);
        } finally {
            functionEvent.commit();
        }
    }
}

/**
 * 処理中のトークンの状態を失敗にする処理
 */
class FailedTokenFunction implements SerializableBiFunction<TokenKey, TokenEntity, TokenEntity> {

    private final Exception cause;

    public FailedTokenFunction(Exception cause) {
        this.cause = cause;
    }

    @Override
    public TokenEntity apply(TokenKey tokenKey, TokenEntity cachedEntity) {
        Event functionEvent = new TokenFunctionEvent("ProcessingTokenFunction", tokenKey.getOwnerId(), tokenKey.getTokenId());
        functionEvent.begin();
        try {
            if (cachedEntity == null) {
                RuntimeException newException = new RuntimeException(String.format("指定されたトークンは存在しません %s", tokenKey.toString()));
                newException.addSuppressed(cause);
                throw newException;
            }
            if (cachedEntity.getStatus().equals(TokenStatus.USED)) {
                RuntimeException newException = new RuntimeException(String.format("キャッシュされているトークンのステータスが使用済みなのに失敗に変更しようとしました %s", tokenKey.toString()));
                newException.addSuppressed(cause);
                throw newException;
            }
            return new TokenEntity(TokenStatus.FAILED);
        } finally {
            functionEvent.commit();
        }
    }
}