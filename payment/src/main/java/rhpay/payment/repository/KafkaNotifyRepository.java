package rhpay.payment.repository;

import rhpay.payment.domain.Payment;
import rhpay.payment.event.PaymentEvent;
import rhpay.monitoring.MonitorRepository;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import javax.enterprise.context.RequestScoped;

@RequestScoped
@MonitorRepository
public class KafkaNotifyRepository implements NotifyRepository {

    @Channel("payment")
    Emitter<PaymentEvent> paymentEmitter;

    public void notify(Payment payment) {
        this.paymentEmitter.send(new PaymentEvent(payment.getBillingDateTime(), payment.getShopperId().value, payment.getStoreId().value, payment.getTokenId().value, payment.getBillingAmount().value));
    }
}
