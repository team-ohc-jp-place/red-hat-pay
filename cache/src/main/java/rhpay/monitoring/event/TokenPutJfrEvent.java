package rhpay.monitoring.event;

import jdk.jfr.Category;
import jdk.jfr.Label;

@Label("TokenPutJfrEvent")
@Category({"RedHatPay", "DataGrid", "Listener"})
public class TokenPutJfrEvent extends TokenGetJfrEvent {

    public TokenPutJfrEvent(String cacheName, boolean originLocal, String typeName, boolean pre, String globalId, long internalId, boolean remote, boolean hasValue, int shopperId, String tokenId) {
        super(cacheName, originLocal, typeName, pre, globalId, internalId, remote, hasValue, shopperId, tokenId);
    }
}
