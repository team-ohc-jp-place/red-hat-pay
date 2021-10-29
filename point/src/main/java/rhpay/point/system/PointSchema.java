package rhpay.point.system;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;
import rhpay.payment.cache.ShopperKey;
import rhpay.point.cache.PointEntity;

@AutoProtoSchemaBuilder(includeClasses = {PointEntity.class, ShopperKey.class}, schemaPackageName = "rhpay.point.cache")
public interface PointSchema extends GeneratedSchema {
}
