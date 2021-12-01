package rhpay.payment.domain;

public class PaymentException extends Exception {
    public PaymentException(String message){
        super(message);
    }
}
