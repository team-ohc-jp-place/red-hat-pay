package rhpay.payment.mock;

import rhpay.payment.domain.*;
import rhpay.payment.repository.TokenRepository;
import rhpay.payment.usecase.TokenPayInput;
import rhpay.payment.usecase.TokenUsecase;

import java.time.LocalDateTime;

public class TokenUsecaseMock implements TokenUsecase {
    @Override
    public Token createToken(ShopperId shopperId, TokenRepository tokenRepository) {
        return new Token(new ShopperId(1), new TokenId("abc"), TokenStatus.UNUSED);
    }

    @Override
    public Payment pay(TokenPayInput input) throws PaymentException {
        return new Payment(new StoreId(1), new ShopperId(2), new Money(3), LocalDateTime.of(2023, 4, 5, 6, 7, 8), new TokenId("abc"));
    }
}
