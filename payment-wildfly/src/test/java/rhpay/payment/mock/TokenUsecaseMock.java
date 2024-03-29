package rhpay.payment.mock;

import rhpay.payment.domain.*;
import rhpay.payment.usecase.TokenPayInput;
import rhpay.payment.usecase.TokenUsecase;

import java.time.LocalDateTime;

public class TokenUsecaseMock implements TokenUsecase {
    @Override
    public Token createToken(ShopperId shopperId) {
        return new Token(shopperId, new TokenId("abc"), TokenStatus.UNUSED);
    }

    @Override
    public Payment pay(TokenPayInput input) throws PaymentException {

        return new Payment(input.storeId, input.shopperId, input.amount, LocalDateTime.now(), input.tokenId);
    }
}
