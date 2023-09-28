package rhpay.monitoring;

import jdk.jfr.Category;
import jdk.jfr.Event;
import jdk.jfr.Label;

@Label("TokenRest")
@Category({"RedHatPay", "Application"})
public class TokenRestEvent extends Event {

    @Label("traceId")
    private final String traceId;

    @Label("method")
    private final String method;

    @Label("shopperId")
    private final int shopperId;

    @Label("tokenId")
    private String tokenId;

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public TokenRestEvent(String traceId, String method, int shopperId) {
        this.traceId = traceId;
        this.method = method;
        this.shopperId = shopperId;
    }

    public TokenRestEvent(String traceId, String method, int shopperId, String tokenId) {
        this.traceId = traceId;
        this.method = method;
        this.shopperId = shopperId;
        this.tokenId = tokenId;
    }
}
