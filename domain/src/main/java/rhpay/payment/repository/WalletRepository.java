package rhpay.payment.repository;

import rhpay.payment.domain.Shopper;
import rhpay.payment.domain.ShopperId;
import rhpay.payment.domain.Wallet;

public interface WalletRepository {

    Wallet load(Shopper owner);

    void store(Wallet wallet);
}
