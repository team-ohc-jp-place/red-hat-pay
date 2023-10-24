package rhpay.payment.repository;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jdk.jfr.Event;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import rhpay.monitoring.TokenRepositoryEvent;
import rhpay.monitoring.TracerService;
import rhpay.payment.domain.Payment;
import rhpay.payment.event.PaymentEvent;

@RequestScoped
public class KafkaNotifyRepository implements NotifyRepository {

    @Channel("payment")
    Emitter<PaymentEvent> paymentEmitter;

    @Inject
    TracerService tracerService;

    public void notify(Payment payment) {
        String traceId = tracerService.traceRepository();

        Event event = new TokenRepositoryEvent(traceId, "notify", payment.getShopperId().value, payment.getTokenId().value);
        event.begin();

        try {
            this.paymentEmitter.send(new PaymentEvent(payment.getBillingDateTime(), payment.getShopperId().value, payment.getStoreId().value, payment.getTokenId().value, payment.getBillingAmount().value));

        } finally {
            event.commit();
            tracerService.closeTrace();
        }
    }
}
