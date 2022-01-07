package rhpay.payment.repository;

import io.quarkus.infinispan.client.Remote;
import jdk.jfr.Event;
import org.infinispan.client.hotrod.RemoteCache;
import rhpay.monitoring.TokenRepositoryEvent;
import rhpay.monitoring.TracerService;
import rhpay.payment.cache.PaymentResponse;
import rhpay.payment.cache.ShopperKey;
import rhpay.payment.cache.WalletEntity;
import rhpay.payment.domain.*;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

@RequestScoped
public class CacheDelegateRepository implements DelegateRepository {

    @Inject
    @Remote("wallet")
    RemoteCache<ShopperKey, WalletEntity> walletCache;

    @Inject
    TracerService tracerService;

    @Override
    public Payment invoke(ShopperId shopperId, TokenId tokenId, CoffeeStore store, Money amount) {

        String traceId = tracerService.traceRepository();

        Event event = new TokenRepositoryEvent(traceId, "invoke", shopperId.value, tokenId.value);
        event.begin();

        try {
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

        } finally {
            event.commit();
            tracerService.closeTrace();
        }
    }
}
