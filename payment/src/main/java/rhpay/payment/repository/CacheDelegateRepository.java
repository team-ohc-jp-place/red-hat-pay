package rhpay.payment.repository;

import io.quarkus.infinispan.client.Remote;
import org.infinispan.client.hotrod.RemoteCache;
import rhpay.payment.cache.PaymentResponse;
import rhpay.payment.cache.TokenEntity;
import rhpay.payment.cache.TokenKey;
import rhpay.payment.domain.*;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

@RequestScoped
public class CacheDelegateRepository implements DelegateRepository{

    @Inject
    @Remote("token")
    RemoteCache<TokenKey, TokenEntity> tokenCache;

    @Override
    public Payment invoke(ShopperId shopperId, TokenId tokenId, CoffeeStore store, Money amount) {

        Map<String, Object> payInfo = new HashMap<>();
        payInfo.put("shopperId", shopperId.value);
        payInfo.put("tokenId", tokenId.value);
        payInfo.put("amount", amount.value);
        payInfo.put("storeId", store.getId().value);
        payInfo.put("storeName", store.getName().value);

        PaymentResponse paymentEntity = tokenCache.execute("PaymentTask", payInfo, new TokenKey(shopperId.value, tokenId.value));

        Payment payment = new Payment(store.getId(), shopperId, tokenId, new Money(paymentEntity.getPillingAmount()), LocalDateTime.ofEpochSecond(paymentEntity.getBillingDateTime(), 0, ZoneOffset.of("+09:00")));

        return payment;
    }
}
