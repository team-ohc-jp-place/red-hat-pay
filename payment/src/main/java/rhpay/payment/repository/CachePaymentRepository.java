package rhpay.payment.repository;

import rhpay.payment.cache.PaymentEntity;
import rhpay.payment.cache.TokenKey;
import rhpay.monitoring.MonitorRepository;
import io.quarkus.infinispan.client.Remote;
import org.infinispan.client.hotrod.RemoteCache;
import rhpay.payment.domain.*;
import rhpay.payment.repository.PaymentRepository;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@RequestScoped
@MonitorRepository
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
        return new Payment(storeId, shopperId, tokenId, billingAmount, billingDateTime);
    }
}
