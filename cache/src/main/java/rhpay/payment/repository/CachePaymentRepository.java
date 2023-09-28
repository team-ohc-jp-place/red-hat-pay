package rhpay.payment.repository;

import jdk.jfr.Event;
import org.infinispan.Cache;
import rhpay.monitoring.SegmentService;
import rhpay.monitoring.event.UseTokenEvent;
import rhpay.payment.cache.PaymentEntity;
import rhpay.payment.cache.TokenKey;
import rhpay.payment.domain.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class CachePaymentRepository implements PaymentRepository {

    private final Cache<TokenKey, PaymentEntity> paymentCache;

    public CachePaymentRepository(Cache<TokenKey, PaymentEntity> paymentCache) {
        this.paymentCache = paymentCache;
    }

    @Override
    public Payment load(ShopperId shopperId, TokenId tokenId) {
        TokenKey key = new TokenKey(shopperId.value, tokenId.value);
        Event event = new UseTokenEvent("loadPayment", key.getOwnerId(), key.getTokenId(), SegmentService.getSegment(paymentCache, key));
        event.begin();
        try {
            PaymentEntity entity = paymentCache.get(key);
            return new Payment(new StoreId(entity.getStoreId()), shopperId, new Money(entity.getBillingAmount()), LocalDateTime.ofEpochSecond(entity.getBillingDateTime(), 0, ZoneOffset.of("+09:00")), tokenId);
        } finally {
            event.commit();
        }
    }

    @Override
    public void store(Payment payment) {
        TokenKey tokenKey = new TokenKey(payment.getShopperId().value, payment.getTokenId().value);
        PaymentEntity paymentEntity = new PaymentEntity(payment.getStoreId().value, payment.getBillingAmount().value, payment.getBillingDateTime().toEpochSecond(ZoneOffset.of("+09:00")));

        Event event = new UseTokenEvent("storePayment", tokenKey.getOwnerId(), tokenKey.getTokenId(), SegmentService.getSegment(paymentCache, tokenKey));
        event.begin();

        try {
            paymentCache.put(tokenKey, paymentEntity);
        } finally {
            event.commit();
        }
    }
}
