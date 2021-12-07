package rhpay.monolith.monitoring;

import jdk.jfr.Category;
import jdk.jfr.Event;
import jdk.jfr.Label;

@Label("Repository")
@Category({"RedHatPay", "Application"})
public class RepositoryEvent extends Event {

    @Label("class")
    private final String className;

    @Label("method")
    private final String method;

    public RepositoryEvent(String className, String method) {
        this.className = className;
        this.method = method;
    }
}

