package rhpay.payment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rhpay.payment.domain.ShopperId;
import rhpay.payment.domain.Token;
import rhpay.payment.domain.TokenStatus;
import rhpay.payment.repository.TokenRepository;
import static org.junit.jupiter.api.Assertions.*;

public class TokenServiceTest {

    @Mock
    TokenRepository tokenRepository;

    @BeforeEach
    void init() throws Exception {
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void createTokenTest(){

        TokenService service = new TokenService(tokenRepository);

        Token token = service.create(new ShopperId(1));

        assertNotNull(token.getShopperId());
        assertEquals(1, token.getShopperId().value);
        assertEquals(TokenStatus.UNUSED, token.getStatus());
        assertNotNull(token.getTokenId());
        assertNotNull(token.getTokenId().value);
    }
}
