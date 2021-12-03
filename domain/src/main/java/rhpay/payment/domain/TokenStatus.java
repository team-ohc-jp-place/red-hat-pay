package rhpay.payment.domain;

public enum TokenStatus {

    UNUSED("未使用"),
    PROCESSING("処理中"),
    USED("使用済み"),
    FAILED("失敗");

    public final String name;

    TokenStatus(String name) {
        this.name = name;
    }
}
