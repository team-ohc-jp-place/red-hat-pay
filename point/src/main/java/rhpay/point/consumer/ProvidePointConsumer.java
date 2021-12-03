package rhpay.point.consumer;

import rhpay.payment.event.PaymentEvent;
import rhpay.monitor.MonitorConsumer;
import rhpay.point.repository.PointDelegateRepository;
import rhpay.point.repository.PointRepository;
import rhpay.point.service.PointDelegateService;
import rhpay.point.service.PointService;
import io.smallrye.common.annotation.NonBlocking;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import rhpay.payment.domain.*;
import rhpay.point.domain.Point;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
@MonitorConsumer
public class ProvidePointConsumer {

    @Inject
    PointDelegateRepository pointDelegateRepository;

    @Incoming("requests")
    @NonBlocking
    public void process(PaymentEvent paymentEvent) throws Exception {

        PointDelegateService pointDelegateService = new PointDelegateService(pointDelegateRepository);

        //TODO どこかから取る
        StoreId storeId = new StoreId(paymentEvent.storeId);
        ShopperId shopperId = new ShopperId(paymentEvent.userId);
        TokenId tokenId = new TokenId(paymentEvent.tokenId);

        Payment payment = new Payment(storeId, shopperId, tokenId, new Money(paymentEvent.amount), paymentEvent.dateTime);

        System.out.println(payment);
        Point point = pointDelegateService.invoke(payment);
        System.out.println(point);
    }
}
