package rhpay.payment.domain;

public enum TokenStatus {

    UNUSED(0, "未使用"),
    PROCESSING(1, "処理中"),
    USED(2, "使用済み"),
    FAILED(3, "失敗");

    public final int value;
    public final String name;

    TokenStatus(int value, String name) {
        this.value = value;
        this.name = name;
    }
}
