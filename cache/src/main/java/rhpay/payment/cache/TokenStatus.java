package rhpay.payment.cache;

import org.infinispan.protostream.annotations.ProtoEnumValue;
import org.infinispan.protostream.annotations.ProtoFactory;

public enum TokenStatus {

    @ProtoEnumValue(value = 0)
    UNUSED,
    @ProtoEnumValue(value = 1)
    PROCESSING,
    @ProtoEnumValue(value = 2)
    USED,
    @ProtoEnumValue(value = 3)
    FAILED;

    public String getName(){
        switch(this){
            case UNUSED:
                return "未使用";
            case PROCESSING:
                return "処理中";
            case USED:
                return "使用済み";
            case FAILED:
                return "エラー";
            default:
                return "想定外のステータス";
        }
    }
}
