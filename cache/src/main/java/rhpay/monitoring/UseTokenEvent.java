package rhpay.monitoring;

import jdk.jfr.Category;
import jdk.jfr.Event;
import jdk.jfr.Label;

@Label("UseTokenEvent")
@Category({"RedHatPay", "DataGrid", "access"})
public class UseTokenEvent extends Event {

    @Label("doing")
    private final String doing;

    @Label("shopperId")
    private final int shopperId;

    @Label("tokenId")
    private final String tokenId;

    @Label("segment")
    private final int segment;

    public UseTokenEvent(String doing, int shopperId, String tokenId, int segment) {
        this.doing = doing;
        this.shopperId = shopperId;
        this.tokenId = tokenId;
        this.segment = segment;
    }
}
