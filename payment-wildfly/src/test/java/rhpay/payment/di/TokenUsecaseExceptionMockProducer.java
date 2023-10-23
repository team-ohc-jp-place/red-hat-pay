package rhpay.payment.di;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import rhpay.payment.mock.TokenUsecaseExceptionMock;
import rhpay.payment.usecase.TokenUsecase;

@Dependent
public class TokenUsecaseExceptionMockProducer {

    @Produces
    public TokenUsecase create(){
        return new TokenUsecaseExceptionMock();
    }
}
