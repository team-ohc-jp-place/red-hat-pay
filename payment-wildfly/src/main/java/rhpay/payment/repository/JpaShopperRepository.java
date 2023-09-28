package rhpay.payment.repository;

import jakarta.enterprise.context.RequestScoped;
import rhpay.payment.domain.FullName;
import rhpay.payment.domain.Shopper;
import rhpay.payment.domain.ShopperId;

//TODO: いつか作る
@RequestScoped
public class JpaShopperRepository implements ShopperRepository{
    @Override
    public Shopper load(ShopperId id) {
        return new Shopper(id, new FullName("abc"));
    }
}
