package rhpay.payment.domain;

public class FailedPaymentException extends Exception {
    public FailedPaymentException(String msg) {
        super(msg);
    }
}
