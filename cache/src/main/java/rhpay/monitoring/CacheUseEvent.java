package rhpay.monitoring;

import jdk.jfr.Category;
import jdk.jfr.Event;
import jdk.jfr.Label;

@Label("CacheUseEvent")
@Category({"RedHatPay", "DataGrid"})
public class CacheUseEvent extends Event {

    @Label("segment")
    private final int segment;

    @Label("doing")
    private final String doing;

    public CacheUseEvent(int segment, String doing) {
        this.segment = segment;
        this.doing = doing;
    }
}
