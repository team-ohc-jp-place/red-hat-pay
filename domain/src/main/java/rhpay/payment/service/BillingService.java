package rhpay.payment.service;

import rhpay.payment.domain.*;
import rhpay.payment.repository.BillingRepository;

public class BillingService {

    private final BillingRepository billingRepository;

    public BillingService(BillingRepository billingRepository) {
        this.billingRepository = billingRepository;
    }

    public Payment bill(Shopper shopper, TokenId tokenId, Billing bill) throws PaymentException {
        return billingRepository.bill(shopper, tokenId, bill);
    }
}
