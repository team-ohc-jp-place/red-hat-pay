package rhpay.monitor;

import jdk.jfr.Category;
import jdk.jfr.Event;
import jdk.jfr.Label;

@Label("Consumer")
@Category("CoffeeStore")
public class ConsumerEvent extends Event {

    @Label("traceId")
    private final String traceId;

    @Label("method")
    private final String method;

    public ConsumerEvent(String traceId, String method) {
        this.traceId = traceId;
        this.method = method;
    }
}
