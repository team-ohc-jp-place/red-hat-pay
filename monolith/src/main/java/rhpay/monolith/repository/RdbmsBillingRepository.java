package rhpay.monolith.repository;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import rhpay.payment.domain.*;
import rhpay.payment.repository.BillingRepository;

@Component
@RequestScope
public class RdbmsBillingRepository implements BillingRepository {
    @Override
    public Payment bill(Shopper shopper, TokenId tokenId, Billing bill) throws PaymentException {
        return null;
    }
}
