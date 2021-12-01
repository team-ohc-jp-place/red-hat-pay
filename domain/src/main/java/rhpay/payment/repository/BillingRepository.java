package rhpay.payment.repository;

import rhpay.payment.domain.*;

public interface BillingRepository {
    Payment bill(ShopperId shopperId, TokenId tokenId, Billing bill) throws PaymentException;
}
