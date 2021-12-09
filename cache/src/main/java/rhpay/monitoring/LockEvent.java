package rhpay.monitoring;

import jdk.jfr.Category;
import jdk.jfr.Event;
import jdk.jfr.Label;

@Label("Lock")
@Category({"RedHatPay", "DataGrid"})
public class LockEvent extends Event {
    @Label("shopperId")
    private final int shopperId;

    public LockEvent(int shopperId) {
        this.shopperId = shopperId;
    }
}
