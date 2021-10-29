package rhpay.payment.repository;

import rhpay.payment.domain.Shopper;
import rhpay.payment.domain.ShopperId;

public interface ShopperRepository {

    Shopper load(final ShopperId id);
}
