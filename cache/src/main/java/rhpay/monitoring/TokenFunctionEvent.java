package rhpay.monitoring;

import jdk.jfr.Category;
import jdk.jfr.Event;
import jdk.jfr.Label;

@Label("TokenFunction")
@Category({"RedHatPay", "DataGrid"})
public class TokenFunctionEvent extends Event {

    @Label("functionName")
    private final String functionName;

    @Label("shopperId")
    private final int shopperId;

    @Label("tokenId")
    private final String tokenId;

    public TokenFunctionEvent(String functionName, int shopperId, String tokenId) {
        this.functionName = functionName;
        this.shopperId = shopperId;
        this.tokenId = tokenId;
    }
}
