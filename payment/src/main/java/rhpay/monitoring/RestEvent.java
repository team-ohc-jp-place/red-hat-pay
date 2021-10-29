package rhpay.monitoring;

import jdk.jfr.Category;
import jdk.jfr.Event;
import jdk.jfr.Label;

@Label("Rest")
@Category("CoffeeStore")
public class RestEvent extends Event {

    @Label("traceId")
    private final String traceId;

    @Label("method")
    private final String method;

    public RestEvent(String traceId, String method) {
        this.traceId = traceId;
        this.method = method;
    }
}
