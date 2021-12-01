package rhpay.payment.service;

import rhpay.payment.domain.*;
import rhpay.payment.repository.BillingRepository;

public class BillingService {

    private final BillingRepository billingRepository;

    public BillingService(BillingRepository billingRepository) {
        this.billingRepository = billingRepository;
    }

    public Payment bill(ShopperId shopperId, TokenId tokenId, Billing bill) throws PaymentException {
        return billingRepository.bill(shopperId, tokenId, bill);
    }
}
