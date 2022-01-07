package rhpay.monitoring;

import jdk.jfr.Category;
import jdk.jfr.Label;

@Label("TokenGetJfrEvent")
@Category({"RedHatPay", "DataGrid", "Listener"})
public class TokenGetJfrEvent extends EntryJfrEvent {

    @Label("shopperId")
    protected final int shopperId;

    @Label("tokenId")
    protected final String tokenId;

    public TokenGetJfrEvent(String cacheName, boolean originLocal, String typeName, boolean pre, String globalId, long internalId, boolean remote, boolean hasValue, int shopperId, String tokenId) {
        super(cacheName, originLocal, typeName, pre, globalId, internalId, remote, hasValue);
        this.shopperId = shopperId;
        this.tokenId = tokenId;
    }
}
