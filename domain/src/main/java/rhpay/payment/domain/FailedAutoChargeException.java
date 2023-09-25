package rhpay.payment.domain;

public class FailedAutoChargeException extends Exception{
    public FailedAutoChargeException(String msg) {
        super(msg);
    }
}
