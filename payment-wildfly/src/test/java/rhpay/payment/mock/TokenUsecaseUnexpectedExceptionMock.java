package rhpay.payment.mock;

import rhpay.payment.domain.Payment;
import rhpay.payment.domain.PaymentException;
import rhpay.payment.domain.ShopperId;
import rhpay.payment.domain.Token;
import rhpay.payment.usecase.TokenPayInput;
import rhpay.payment.usecase.TokenUsecase;

public class TokenUsecaseUnexpectedExceptionMock implements TokenUsecase {
    @Override
    public Token createToken(ShopperId shopperId) {
        throw new RuntimeException("");
    }

    @Override
    public Payment pay(TokenPayInput input) throws PaymentException {
        throw new RuntimeException();
    }
}
