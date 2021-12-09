package rhpay.monitoring;

import jdk.jfr.Category;
import jdk.jfr.Event;
import jdk.jfr.Label;

@Label("DistributedTask")
@Category({"RedHatPay", "DataGrid"})
public class DistributedTaskEvent extends Event {

    @Label("taskName")
    private final String taskName;

    @Label("shopperId")
    private final int shopperId;

    @Label("tokenId")
    private final String tokenId;

    public DistributedTaskEvent(String taskName, int shopperId, String tokenId) {
        this.taskName = taskName;
        this.shopperId = shopperId;
        this.tokenId = tokenId;
    }
}
