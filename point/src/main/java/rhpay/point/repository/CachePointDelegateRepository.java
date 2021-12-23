package rhpay.point.repository;

import io.opentracing.Tracer;
import rhpay.payment.domain.Payment;
import rhpay.point.domain.Point;
import rhpay.point.cache.PointEntity;
import rhpay.payment.cache.ShopperKey;
import rhpay.monitor.MonitorRepository;
import io.quarkus.infinispan.client.Remote;
import org.infinispan.client.hotrod.RemoteCache;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

@RequestScoped
@MonitorRepository
public class CachePointDelegateRepository implements PointDelegateRepository {

    @Inject
    Tracer tracer;

    @Inject
    @Remote("point")
    RemoteCache<ShopperKey, PointEntity> pointCache;

    public Point invoke(Payment payment) {

        String traceId = (tracer == null || tracer.activeSpan() == null || tracer.activeSpan().context() == null || tracer.activeSpan().context().toTraceId() == null) ? "" : tracer.activeSpan().context().toTraceId();

        Map<String, Object> payInfo = new HashMap<>();
        payInfo.put("traceId", traceId);
        payInfo.put("ownerId", payment.getShopperId().value);
        payInfo.put("amount", payment.getBillingAmount().value);
        payInfo.put("storeId", payment.getStoreId().value);
        payInfo.put("tokenId", payment.getTokenId().value);
        payInfo.put("epoch", payment.getBillingDateTime().toEpochSecond(ZoneOffset.of("+09:00")));

        PointEntity pointEntity = pointCache.execute("PointTask", payInfo, new ShopperKey(payment.getShopperId().value));

        return new Point(payment.getShopperId(), pointEntity.getAmount());
    }
}
