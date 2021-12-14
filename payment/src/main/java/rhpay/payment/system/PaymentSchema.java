package rhpay.payment.system;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;
import rhpay.payment.cache.*;
import rhpay.payment.repository.function.*;

@AutoProtoSchemaBuilder(includeClasses = {ShopperEntity.class, PaymentEntity.class, PaymentResponse.class, WalletEntity.class, ShopperKey.class, TokenKey.class, TokenEntity.class, TokenStatus.class, FailedTokenFunction.class, UsedTokenFunction.class, ProcessingTokenFunction.class}, schemaPackageName = "rhpay.payment.cache", autoImportClasses=true)
public interface PaymentSchema extends GeneratedSchema {
}
