package rhpay.monitoring.event;

import jdk.jfr.Category;
import jdk.jfr.Event;
import jdk.jfr.Label;

@Label("UseShopperEvent")
@Category({"RedHatPay", "DataGrid", "access"})
public class UseShopperEvent extends Event {

    @Label("doing")
    private final String doing;

    @Label("shopperId")
    private final int shopperId;

    @Label("segment")
    private final int segment;

    public UseShopperEvent(String doing, int shopperId, int segment) {
        this.doing = doing;
        this.shopperId = shopperId;
        this.segment = segment;
    }
}
