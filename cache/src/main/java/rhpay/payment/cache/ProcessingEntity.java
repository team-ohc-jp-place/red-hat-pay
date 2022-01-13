package rhpay.payment.cache;

import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

public class ProcessingEntity {

    private final String processId;

    @ProtoFactory
    public ProcessingEntity(String processId) {
        this.processId = processId;
    }

    @ProtoField(number = 1)
    public String getProcessId() {
        return processId;
    }
}
