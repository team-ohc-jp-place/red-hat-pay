package rhpay.payment.cache;

import org.infinispan.protostream.annotations.ProtoEnumValue;

public enum TokenStatus {

    @ProtoEnumValue(value = 0)
    UNUSED("未使用"),
    @ProtoEnumValue(value = 1)
    PROCESSING("処理中"),
    @ProtoEnumValue(value = 2)
    USED("使用済み"),
    @ProtoEnumValue(value = 3)
    FAILED("失敗");

    public final String name;

    TokenStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public rhpay.payment.domain.TokenStatus toDomain(){
        switch(this){
            case UNUSED: return rhpay.payment.domain.TokenStatus.UNUSED;
            case PROCESSING: return rhpay.payment.domain.TokenStatus.PROCESSING;
            case USED: return rhpay.payment.domain.TokenStatus.USED;
        }
        return rhpay.payment.domain.TokenStatus.FAILED;
    }

    public static TokenStatus fromDomain(rhpay.payment.domain.TokenStatus status){
        switch(status){
            case UNUSED: return TokenStatus.UNUSED;
            case PROCESSING: return TokenStatus.PROCESSING;
            case USED: return TokenStatus.USED;
        }
        return TokenStatus.FAILED;
    }
}
