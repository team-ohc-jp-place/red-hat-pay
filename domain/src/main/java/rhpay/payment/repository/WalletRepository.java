package rhpay.payment.repository;

import rhpay.payment.domain.ShopperId;
import rhpay.payment.domain.Wallet;

public interface WalletRepository {

    Wallet load(ShopperId ownerId);

}
