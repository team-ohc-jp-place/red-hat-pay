package rhpay.payment.service;

import rhpay.payment.domain.Shopper;
import rhpay.payment.repository.WalletRepository;
import rhpay.payment.domain.ShopperId;
import rhpay.payment.domain.Wallet;

public class WalletService {

    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Wallet load(Shopper shopper) {
        return walletRepository.load(shopper);
    }

    public void store(Wallet wallet) {
        walletRepository.store(wallet);
    }
}
