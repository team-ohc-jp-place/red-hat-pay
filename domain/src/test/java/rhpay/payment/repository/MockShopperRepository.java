package rhpay.payment.repository;

import rhpay.payment.domain.Shopper;
import rhpay.payment.domain.ShopperId;

public class MockShopperRepository implements ShopperRepository{
    @Override
    public Shopper load(ShopperId id) {
        return null;
    }
}
