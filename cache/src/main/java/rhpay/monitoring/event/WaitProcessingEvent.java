package rhpay.monitoring.event;

import jdk.jfr.Event;

public class WaitProcessingEvent extends Event {

    private final String traceId;
    private final int retryNo;
    private final String tokenId;
    private final int shopperId;

    public WaitProcessingEvent(String traceId, int retryNo, String tokenId, int shopperId) {
        this.traceId = traceId;
        this.retryNo = retryNo;
        this.tokenId = tokenId;
        this.shopperId = shopperId;
    }
}
