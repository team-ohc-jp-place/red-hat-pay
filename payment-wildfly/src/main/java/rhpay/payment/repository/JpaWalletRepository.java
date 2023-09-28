package rhpay.payment.repository;

import jakarta.enterprise.context.RequestScoped;
import rhpay.payment.domain.Money;
import rhpay.payment.domain.Shopper;
import rhpay.payment.domain.Wallet;

//TODO: いつか作る
@RequestScoped
public class JpaWalletRepository implements WalletRepository{
    @Override
    public Wallet load(Shopper owner) {
        return new Wallet(owner, new Money(100), new Money(10));
    }

    @Override
    public void store(Wallet wallet) {
        System.out.println(wallet);
    }
}
