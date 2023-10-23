package rhpay.payment.mock;

import rhpay.payment.domain.*;
import rhpay.payment.usecase.TokenPayInput;
import rhpay.payment.usecase.TokenUsecase;

public class TokenUsecaseExceptionMock implements TokenUsecase {
    @Override
    public Token createToken(ShopperId shopperId) {
        return null;
    }

    @Override
    public Payment pay(TokenPayInput input) throws PaymentException {
        throw new PaymentException("Fail to pay");
    }
}
