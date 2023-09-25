package rhpay.payment.repository;

import jakarta.enterprise.context.RequestScoped;
import rhpay.payment.domain.*;

import java.time.LocalDateTime;

//TODO: いつか作る
@RequestScoped
public class JpaPaymentRepository implements PaymentRepository{
    @Override
    public Payment load(ShopperId shopperId, TokenId tokenId) {
        return new Payment(new StoreId(1), shopperId, new Money(1), LocalDateTime.now(), tokenId);
    }

    @Override
    public void store(Payment payment) {
        System.out.println(payment);
    }
}
