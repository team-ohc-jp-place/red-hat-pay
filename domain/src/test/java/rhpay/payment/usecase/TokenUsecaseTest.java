package rhpay.payment.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import rhpay.payment.domain.*;
import rhpay.payment.repository.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

public class TokenUsecaseTest {

    @BeforeEach
    void init() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void pay() throws TokenException {
        ShopperId shopperId = new ShopperId(1);
        TokenId tokenId = new TokenId("a");
        StoreId storeId = new StoreId(1);

        List<Token> tokenList = new ArrayList();
        tokenList.add(new Token(shopperId, tokenId, TokenStatus.UNUSED));
        MockTokenRepository tokenRepository = new MockTokenRepository(tokenList);
        MockWalletRepository walletRepository = new MockWalletRepository();
        MockPaymentRepository paymentRepository = new MockPaymentRepository();
        MockShopperRepository shopperRepository = new MockShopperRepository();
        MockCoffeeStoreRepository coffeeStoreRepository = new MockCoffeeStoreRepository();

        TokenPayInput input = new TokenPayInput(shopperId, new Money(4), tokenId, storeId);

        TokenUsecaseImpl usecase = new TokenUsecaseImpl(tokenRepository, coffeeStoreRepository, shopperRepository, paymentRepository, walletRepository);

        try {
            Payment payment = usecase.pay(input);
        } catch (PaymentException e) {
            fail();
        }


    }
}
