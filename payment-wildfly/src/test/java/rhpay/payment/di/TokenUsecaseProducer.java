package rhpay.payment.di;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import rhpay.payment.repository.*;
import rhpay.payment.usecase.TokenUsecase;
import rhpay.payment.usecase.TokenUsecaseImpl;

@Dependent
public class TokenUsecaseProducer {

    @Produces
    public TokenUsecase create(ShopperRepository shopperRepository, WalletRepository walletRepository, TokenRepository tokenRepository, CoffeeStoreRepository coffeeStoreRepository, PaymentRepository paymentRepository){
        return new TokenUsecaseImpl(tokenRepository, coffeeStoreRepository, shopperRepository, paymentRepository, walletRepository);
    }
}
