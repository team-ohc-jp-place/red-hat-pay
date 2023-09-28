package rhpay.payment.di;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import rhpay.payment.mock.TokenUsecaseMock;
import rhpay.payment.usecase.TokenUsecase;


@Dependent
public class TokenUsecaseMockProducer {

    @Produces
    public TokenUsecase create(){
        return new TokenUsecaseMock();
    }
}
