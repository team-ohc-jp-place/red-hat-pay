package rhpay.payment.repository;

import rhpay.payment.domain.Money;
import rhpay.payment.domain.Shopper;
import rhpay.payment.domain.Wallet;

public class MockWalletRepository implements WalletRepository{
    @Override
    public Wallet load(Shopper owner) {
        return new Wallet(owner, new Money(10), new Money(10));
    }

    @Override
    public void store(Wallet wallet) {

    }
}
