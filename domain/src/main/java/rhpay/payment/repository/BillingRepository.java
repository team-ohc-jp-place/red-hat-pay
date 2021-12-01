package rhpay.payment.repository;

import rhpay.payment.domain.*;

public interface BillingRepository {
    Payment bill(Shopper shopper, TokenId tokenId, Billing bill) throws PaymentException;
}
