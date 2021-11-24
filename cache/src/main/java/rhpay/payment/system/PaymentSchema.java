package rhpay.payment.system;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;
import rhpay.payment.cache.*;

@AutoProtoSchemaBuilder(includeClasses = {ShopperEntity.class, PaymentEntity.class, PaymentResponse.class, WalletEntity.class, ShopperKey.class, TokenKey.class, TokenEntity.class, TokenStatus.class}, schemaPackageName = "rhpay.payment.cache", schemaFileName = "PaymentSchema.proto", schemaFilePath = "proto/")
public interface PaymentSchema extends GeneratedSchema {
}
