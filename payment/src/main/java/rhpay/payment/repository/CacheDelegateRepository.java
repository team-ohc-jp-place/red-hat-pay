package rhpay.payment.repository;

import io.opentracing.Tracer;
import io.quarkus.infinispan.client.Remote;
import org.infinispan.Cache;
import org.infinispan.CacheStream;
import org.infinispan.client.hotrod.RemoteCache;
import rhpay.payment.cache.*;
import rhpay.payment.domain.*;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@RequestScoped
public class CacheDelegateRepository implements DelegateRepository {

    @Inject
    Tracer tracer;

    @Inject
    @Remote("wallet")
    RemoteCache<ShopperKey, WalletEntity> walletCache;

    @Override
    public Payment invoke(ShopperId shopperId, TokenId tokenId, CoffeeStore store, Money amount) {

        String traceId = (tracer == null || tracer.activeSpan() == null || tracer.activeSpan().context() == null || tracer.activeSpan().context().toTraceId() == null) ? "" : tracer.activeSpan().context().toTraceId();
        Map<String, Object> payInfo = new HashMap<>();
        payInfo.put("traceId", traceId);
        payInfo.put("shopperId", shopperId.value);
        payInfo.put("tokenId", tokenId.value);
        payInfo.put("amount", amount.value);
        payInfo.put("storeId", store.getId().value);
        payInfo.put("storeName", store.getName().value);

        PaymentResponse paymentEntity = walletCache.execute("PaymentTask", payInfo, new ShopperKey(shopperId.value));

        Payment payment = new Payment(store.getId(), shopperId, tokenId, new Money(paymentEntity.getPillingAmount()), LocalDateTime.ofEpochSecond(paymentEntity.getBillingDateTime(), 0, ZoneOffset.of("+09:00")));

        return payment;
    }
}
