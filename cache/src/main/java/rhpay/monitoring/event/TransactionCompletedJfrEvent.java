package rhpay.monitoring.event;

import jdk.jfr.Category;
import jdk.jfr.Enabled;
import jdk.jfr.Label;

@Label("TransactionCompletedJfrEvent")
@Category({"RedHatPay", "DataGrid", "Listener"})
@Enabled(value = false)
public class TransactionCompletedJfrEvent extends TransactionRegisteredJfrEvent {

    @Label("transactionSuccessful")
    protected final boolean transactionSuccessful;

    public TransactionCompletedJfrEvent(String cacheName, boolean originLocal, String typeName, boolean pre, String globalId, long internalId, boolean remote, boolean transactionSuccessful) {
        super(cacheName, originLocal, typeName, pre, globalId, internalId, remote);
        this.transactionSuccessful = transactionSuccessful;
    }
}
