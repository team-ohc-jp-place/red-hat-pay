package rhpay.monitoring.event;

import jdk.jfr.Category;
import jdk.jfr.Enabled;
import jdk.jfr.Event;
import jdk.jfr.Label;

@Label("CallDistributedTask")
@Category({"RedHatPay", "DataGrid"})
@Enabled(value = false)
public class CallDistributedTaskEvent extends Event {

    @Label("segment")
    private final int segment;

    @Label("doing")
    private final String doing;

    public CallDistributedTaskEvent(int segment, String doing) {
        this.segment = segment;
        this.doing = doing;
    }
}
