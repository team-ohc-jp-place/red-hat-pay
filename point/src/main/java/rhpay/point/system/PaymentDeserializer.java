package rhpay.point.system;

import rhpay.payment.event.PaymentEvent;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class PaymentDeserializer extends ObjectMapperDeserializer<PaymentEvent> {
    public PaymentDeserializer() {
        super(PaymentEvent.class);
    }
}