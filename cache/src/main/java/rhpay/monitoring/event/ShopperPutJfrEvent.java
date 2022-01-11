package rhpay.monitoring.event;

import jdk.jfr.Category;
import jdk.jfr.Label;

@Label("ShopperPutJfrEvent")
@Category({"RedHatPay", "DataGrid", "Listener"})
public class ShopperPutJfrEvent extends ShopperGetJfrEvent {

    public ShopperPutJfrEvent(String cacheName, boolean originLocal, String typeName, boolean pre, String globalId, long internalId, boolean remote, boolean hasValue, int shopperId) {
        super(cacheName, originLocal, typeName, pre, globalId, internalId, remote, hasValue, shopperId);
    }
}
