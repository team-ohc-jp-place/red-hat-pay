package rhpay.point;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;
import rhpay.point.cache.PointEntity;

@AutoProtoSchemaBuilder(includeClasses = {PointEntity.class}, schemaPackageName = "rhpay.point.cache", schemaFileName = "PointSchema.proto", schemaFilePath = "proto/")
public interface PointSchema extends GeneratedSchema {
}
