package rhpay.payment.usecase;

import rhpay.payment.domain.Payment;
import rhpay.payment.domain.PaymentException;
import rhpay.payment.domain.ShopperId;
import rhpay.payment.domain.Token;
import rhpay.payment.repository.TokenRepository;

public interface TokenUsecase {
    Token createToken(ShopperId shopperId, TokenRepository tokenRepository);
    Payment pay(TokenPayInput input) throws PaymentException;
}
