package rhpay.payment.repository;

import rhpay.payment.domain.Billing;
import rhpay.payment.domain.Payment;
import rhpay.payment.domain.ShopperId;
import rhpay.payment.domain.TokenId;

public interface BillingRepository {
    Payment bill(ShopperId shopperId, TokenId tokenId, Billing bill);
}
