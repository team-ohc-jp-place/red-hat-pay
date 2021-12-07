package rhpay.monitoring;

import jdk.jfr.Category;
import jdk.jfr.Event;
import jdk.jfr.Label;

@Label("ShopperFunction")
@Category({"RedHatPay", "DataGrid"})
public class ShopperFunctionEvent extends Event {

    @Label("functionName")
    private final String functionName;

    @Label("shopperId")
    private final int shopperId;

    public ShopperFunctionEvent(String functionName, int shopperId) {
        this.functionName = functionName;
        this.shopperId = shopperId;
    }
}
