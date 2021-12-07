package rhpay.payment.domain;

public enum TokenStatus {

    UNUSED("未使用", 1),
    PROCESSING("処理中", 2),
    USED("使用済み", 3),
    FAILED("失敗", 4);

    public final String name;
    public final int value;

    TokenStatus(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public static TokenStatus getInstance(int value){
        if(value == UNUSED.value) return UNUSED;
        if(value == PROCESSING.value) return PROCESSING;
        if(value == USED.value) return USED;
        if(value == FAILED.value) return FAILED;
        throw new RuntimeException("invalid token value");
    }
}
