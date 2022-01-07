package rhpay.monitoring;

import jdk.jfr.Category;
import jdk.jfr.Event;
import jdk.jfr.Label;

@Label("TransactionRegisteredEvent")
@Category({"RedHatPay", "DataGrid", "Listener"})
public class TransactionRegisteredJfrEvent extends Event {

    @Label("cacheName")
    protected final String cacheName;
    @Label("originLocal")
    protected final boolean originLocal;
    @Label("typeName")
    protected final String typeName;
    @Label("pre")
    protected final boolean pre;
    @Label("globalId")
    protected final String globalId;
    @Label("internalId")
    protected final long internalId;
    @Label("remote")
    protected final boolean remote;

    public TransactionRegisteredJfrEvent(String cacheName, boolean originLocal, String typeName, boolean pre, String globalId, long internalId, boolean remote) {
        this.cacheName = cacheName;
        this.originLocal = originLocal;
        this.typeName = typeName;
        this.pre = pre;
        this.globalId = globalId;
        this.internalId = internalId;
        this.remote = remote;
    }
}
