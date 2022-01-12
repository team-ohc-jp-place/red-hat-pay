package rhpay.monitoring.event;

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

    @Label("traceId")
    private final String traceId;

    @Label("tryCount")
    private final int tryCount;

    public DistributedTaskEvent(String taskName, int shopperId, String tokenId, String traceId, int tryCount) {
        this.taskName = taskName;
        this.shopperId = shopperId;
        this.tokenId = tokenId;
        this.traceId = traceId;
        this.tryCount = tryCount;
    }
}
