package rhpay.payment.task;

import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.util.function.SerializableBiFunction;
import rhpay.payment.cache.ProcessingEntity;
import rhpay.payment.cache.ShopperKey;

public class ProcessingFunction implements SerializableBiFunction<ShopperKey, ProcessingEntity, ProcessingEntity> {

    private final String processingId;

    @ProtoFactory
    public ProcessingFunction(String processingId) {
        this.processingId = processingId;
    }

    @ProtoField(number = 1)
    public String getProcessingId() {
        return processingId;
    }


    @Override
    public ProcessingEntity apply(ShopperKey shopperKey, ProcessingEntity processingEntity) {
        if (processingEntity == null) {
            return new ProcessingEntity(processingId);
        }
        return processingEntity;
    }
}
