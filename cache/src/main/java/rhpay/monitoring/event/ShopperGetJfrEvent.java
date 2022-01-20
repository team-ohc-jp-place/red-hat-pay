package rhpay.monitoring.event;

import jdk.jfr.Category;
import jdk.jfr.Enabled;
import jdk.jfr.Label;

@Label("ShopperGetJfrEvent")
@Category({"RedHatPay", "DataGrid", "Listener"})
@Enabled(value = false)
public class ShopperGetJfrEvent extends EntryJfrEvent {

    @Label("shopperId")
    protected final int shopperId;

    public ShopperGetJfrEvent(String cacheName, boolean originLocal, String typeName, boolean pre, String globalId, long internalId, boolean remote, boolean hasValue, int shopperId) {
        super(cacheName, originLocal, typeName, pre, globalId, internalId, remote, hasValue);
        this.shopperId = shopperId;
    }
}
