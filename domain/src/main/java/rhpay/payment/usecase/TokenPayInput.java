package rhpay.payment.usecase;

import rhpay.payment.domain.Money;
import rhpay.payment.domain.ShopperId;
import rhpay.payment.domain.StoreId;
import rhpay.payment.domain.TokenId;
import rhpay.payment.repository.*;

public class TokenPayInput {

    public TokenPayInput(TokenRepository tokenRepository, CoffeeStoreRepository coffeeStoreRepository, ShopperRepository shopperRepository, PaymentRepository paymentRepository, WalletRepository walletRepository, ShopperId shopperId, Money amount, TokenId tokenId, StoreId storeId) {
        this.tokenRepository = tokenRepository;
        this.coffeeStoreRepository = coffeeStoreRepository;
        this.shopperRepository = shopperRepository;
        this.paymentRepository = paymentRepository;
        this.walletRepository = walletRepository;
        this.shopperId = shopperId;
        this.amount = amount;
        this.tokenId = tokenId;
        this.storeId = storeId;
    }

    public final TokenRepository tokenRepository;
    public final CoffeeStoreRepository coffeeStoreRepository;
    public final ShopperRepository shopperRepository;
    public final PaymentRepository paymentRepository;
    public final WalletRepository walletRepository;

    public final ShopperId shopperId;
    public final Money amount;
    public final TokenId tokenId;
    public final StoreId storeId;
}
