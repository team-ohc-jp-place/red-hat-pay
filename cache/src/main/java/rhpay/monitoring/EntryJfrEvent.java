package rhpay.monitoring;

import jdk.jfr.Category;
import jdk.jfr.Label;

@Label("EntryJfrEvent")
@Category({"RedHatPay", "DataGrid", "Listener"})
public class EntryJfrEvent extends TransactionRegisteredJfrEvent {

    @Label("hasValue")
    protected final boolean hasValue;

    public EntryJfrEvent(String cacheName, boolean originLocal, String typeName, boolean pre, String globalId, long internalId, boolean remote, boolean hasValue) {
        super(cacheName, originLocal, typeName, pre, globalId, internalId, remote);
        this.hasValue = hasValue;
    }
}
