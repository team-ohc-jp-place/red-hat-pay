package rhpay.monolith.monitoring;

import jdk.jfr.Category;
import jdk.jfr.Event;
import jdk.jfr.Label;

@Label("Payment")
@Category({"RedHatPay", "Domain"})
public class PaymentEvent extends Event {
}
