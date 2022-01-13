package rhpay.payment.system;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;
import rhpay.payment.cache.*;
import rhpay.payment.task.PaymentFunction;
import rhpay.payment.task.ProcessingFunction;

@AutoProtoSchemaBuilder(includeClasses = {ShopperEntity.class, PaymentEntity.class, PaymentResponse.class, WalletEntity.class, ShopperKey.class, TokenKey.class, TokenEntity.class, TokenStatus.class, PaymentFunction.class, ProcessingEntity.class, ProcessingFunction.class}, schemaPackageName = "rhpay.payment.cache", autoImportClasses = true)
public interface PaymentSchema extends GeneratedSchema {
}
