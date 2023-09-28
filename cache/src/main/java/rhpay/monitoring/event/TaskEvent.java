package rhpay.monitoring.event;

import jdk.jfr.Category;
import jdk.jfr.Event;
import jdk.jfr.Label;

@Label("ServerTask")
@Category({"RedHatPay", "DataGrid"})
public class TaskEvent extends Event {

    @Label("traceId")
    private final String traceId;

    @Label("taskName")
    private final String taskName;

    @Label("shopperId")
    private final int shopperId;

    public TaskEvent(String traceId, String taskName, int shopperId) {
        this.traceId = traceId;
        this.taskName = taskName;
        this.shopperId = shopperId;
    }
}
