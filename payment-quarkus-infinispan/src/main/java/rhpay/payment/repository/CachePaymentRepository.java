package rhpay.payment.repository;

import io.quarkus.infinispan.client.Remote;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.infinispan.client.hotrod.RemoteCache;
import rhpay.payment.cache.PaymentEntity;
import rhpay.payment.cache.TokenKey;
import rhpay.payment.domain.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@RequestScoped
public class CachePaymentRepository implements PaymentRepository {

    @Inject
    @Remote("payment")
    RemoteCache<TokenKey, PaymentEntity> paymentCache;

    @Override
    public Payment load(ShopperId shopperId, TokenId tokenId) {
        PaymentEntity paymentEntity = paymentCache.get(new TokenKey(shopperId.value, tokenId.value));
        if (paymentEntity == null) {
            return null;
        }
        StoreId storeId = new StoreId(paymentEntity.getStoreId());
        Money billingAmount = new Money(paymentEntity.getBillingAmount());
        LocalDateTime billingDateTime = LocalDateTime.ofEpochSecond(paymentEntity.getBillingDateTime(), 0, ZoneOffset.of("+09:00"));
        return new Payment(storeId, shopperId, billingAmount, billingDateTime, tokenId);
    }

    @Override
    public void store(Payment payment) {
        throw new UnsupportedOperationException();
    }
}
