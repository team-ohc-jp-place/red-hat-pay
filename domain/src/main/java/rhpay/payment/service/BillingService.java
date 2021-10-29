package rhpay.payment.service;

import rhpay.payment.repository.BillingRepository;
import rhpay.payment.domain.Billing;
import rhpay.payment.domain.Payment;
import rhpay.payment.domain.ShopperId;
import rhpay.payment.domain.TokenId;

public class BillingService {

    private final BillingRepository billingRepository;

    public BillingService(BillingRepository billingRepository) {
        this.billingRepository = billingRepository;
    }

    public Payment bill(ShopperId shopperId, TokenId tokenId, Billing bill) {
        return billingRepository.bill(shopperId, tokenId, bill);
    }
}
