package rhpay.monitoring.event;

import jdk.jfr.Category;
import jdk.jfr.Enabled;
import jdk.jfr.Label;

@Label("EntryJfrEvent")
@Category({"RedHatPay", "DataGrid", "Listener"})
@Enabled(value = false)
public class EntryJfrEvent extends TransactionRegisteredJfrEvent {

    @Label("hasValue")
    protected final boolean hasValue;

    public EntryJfrEvent(String cacheName, boolean originLocal, String typeName, boolean pre, String globalId, long internalId, boolean remote, boolean hasValue) {
        super(cacheName, originLocal, typeName, pre, globalId, internalId, remote);
        this.hasValue = hasValue;
    }
}
