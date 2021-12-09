package rhpay.payment.repository;

import jdk.jfr.Event;
import org.infinispan.Cache;
import rhpay.monitoring.CacheUseEvent;
import rhpay.monitoring.SegmentService;
import rhpay.payment.cache.PaymentEntity;
import rhpay.payment.cache.TokenKey;
import rhpay.payment.domain.Payment;
import rhpay.payment.domain.ShopperId;
import rhpay.payment.domain.TokenId;

import java.time.ZoneOffset;

public class CachePaymentRepository implements PaymentRepository{

    private final Cache<TokenKey, PaymentEntity> paymentCache;

    public CachePaymentRepository(Cache<TokenKey, PaymentEntity> paymentCache) {
        this.paymentCache = paymentCache;
    }

    @Override
    public Payment load(ShopperId shopperId, TokenId tokenId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void store(Payment payment) {
        TokenKey tokenKey = new TokenKey(payment.getShopperId().value, payment.getTokenId().value);
        PaymentEntity paymentEntity = new PaymentEntity(payment.getStoreId().value, payment.getBillingAmount().value, payment.getBillingDateTime().toEpochSecond(ZoneOffset.of("+09:00")));
        Event event = new CacheUseEvent(SegmentService.getSegment(paymentCache, tokenKey), "storePayment");
        event.begin();
        paymentCache.put(tokenKey, paymentEntity);
        event.commit();
    }
}
