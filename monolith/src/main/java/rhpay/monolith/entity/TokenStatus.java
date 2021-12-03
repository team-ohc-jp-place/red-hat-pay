package rhpay.monolith.entity;

public enum TokenStatus {

    UNUSED,
    PROCESSING,
    USED,
    FAILED;

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
