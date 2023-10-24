package rhpay.monitoring;

import jdk.jfr.Category;
import jdk.jfr.Event;
import jdk.jfr.Label;

@Label("TokenRepositoryEvent")
@Category({"RedHatPay", "Application"})
public class TokenRepositoryEvent extends Event {

    @Label("traceId")
    private final String traceId;

    @Label("method")
    private final String method;

    @Label("shopperId")
    private final int shopperId;

    @Label("tokenId")
    private String tokenId;


    public TokenRepositoryEvent(String traceId, String method, int shopperId) {
        this.traceId = traceId;
        this.method = method;
        this.shopperId = shopperId;
    }

    public TokenRepositoryEvent(String traceId, String method, int shopperId, String tokenId) {
        this.traceId = traceId;
        this.method = method;
        this.shopperId = shopperId;
        this.tokenId = tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }
}
