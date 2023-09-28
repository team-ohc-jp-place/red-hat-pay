package rhpay.payment.repository;

import rhpay.payment.domain.Shopper;
import rhpay.payment.domain.Wallet;

public class MockWalletRepository implements WalletRepository{
    @Override
    public Wallet load(Shopper owner) {
        return null;
    }

    @Override
    public void store(Wallet wallet) {

    }
}
