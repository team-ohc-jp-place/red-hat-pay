package rhpay.monitoring;

import jdk.jfr.Category;
import jdk.jfr.Event;
import jdk.jfr.Label;

@Label("Repository")
@Category({"RedHatPay", "Application"})
public class RepositoryEvent extends Event {

    @Label("traceId")
    private final String traceId;

    @Label("class")
    private final String className;

    @Label("method")
    private final String method;

    public RepositoryEvent(String traceId, String className, String method) {
        this.traceId = traceId;
        this.className = className;
        this.method = method;
    }
}

