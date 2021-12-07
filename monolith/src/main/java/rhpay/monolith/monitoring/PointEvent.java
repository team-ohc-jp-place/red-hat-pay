package rhpay.monolith.monitoring;

import jdk.jfr.Category;
import jdk.jfr.Event;
import jdk.jfr.Label;

@Label("Point")
@Category({"RedHatPay", "Domain"})
public class PointEvent extends Event {
}
