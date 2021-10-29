package rhpay.loadtest.system;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;
import rhpay.payment.cache.*;

@AutoProtoSchemaBuilder(includeClasses = {ShopperEntity.class, PaymentResponse.class, WalletEntity.class, ShopperKey.class, TokenKey.class, TokenEntity.class, TokenStatus.class, PaymentEntity.class}, schemaPackageName = "rhpay.payment.cache")
public interface PaymentSchema extends GeneratedSchema {
}
