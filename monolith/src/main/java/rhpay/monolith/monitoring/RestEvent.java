package rhpay.monolith.monitoring;

import jdk.jfr.Category;
import jdk.jfr.Event;
import jdk.jfr.Label;

@Label("Rest")
@Category({"RedHatPay", "Application"})
public class RestEvent extends Event {

    @Label("class")
    private final String className;

    @Label("method")
    private final String method;

    public RestEvent(String className, String method) {
        this.className = className;
        this.method = method;
    }
}
