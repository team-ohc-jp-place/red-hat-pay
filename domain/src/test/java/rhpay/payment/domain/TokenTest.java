package rhpay.payment.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TokenTest {

    private ShopperId SHOPPER_ID = new ShopperId(1);
    private TokenId TOKEN_ID = new TokenId("1");

    private Payment PAYMENT = new Payment(new StoreId(1), SHOPPER_ID, new Money(1), LocalDateTime.now());

    @Test
    @DisplayName("各状態のトークンを処理中にする")
    public void processing() {
        Token unusedToken = new Token(SHOPPER_ID, TOKEN_ID, TokenStatus.UNUSED);
        Token processingToken = new Token(SHOPPER_ID, TOKEN_ID, TokenStatus.PROCESSING);
        Token usedToken = new Token(SHOPPER_ID, TOKEN_ID, TokenStatus.USED);
        Token failtToken = new Token(SHOPPER_ID, TOKEN_ID, TokenStatus.FAILED);

        Token t1 = null;
        try {
            t1 = unusedToken.processing();
        } catch (TokenException e) {
            fail();
        }
        assertNotNull(t1);
        assertEquals(TokenStatus.PROCESSING, t1.getStatus());

        Token t2 = null;
        try {
            t2 = processingToken.processing();
            fail();
        } catch (TokenException e) {
        }
        assertNull(t2);

        Token t3 = null;
        try {
            t3 = usedToken.processing();
            fail();
        } catch (TokenException e) {
        }
        assertNull(t3);

        Token t4 = null;
        try {
            t4 = failtToken.processing();
            fail();
        } catch (TokenException e) {
        }
        assertNull(t4);
    }

    @Test
    @DisplayName("各状態のトークンを使用済みにする")
    public void used() {
        Token unusedToken = new Token(SHOPPER_ID, TOKEN_ID, TokenStatus.UNUSED);
        Token processingToken = new Token(SHOPPER_ID, TOKEN_ID, TokenStatus.PROCESSING);
        Token usedToken = new Token(SHOPPER_ID, TOKEN_ID, TokenStatus.USED);
        Token failtToken = new Token(SHOPPER_ID, TOKEN_ID, TokenStatus.FAILED);

        Token t1 = null;
        try {
            t1 = unusedToken.used(PAYMENT);
            fail();
        } catch (TokenException e) {
        }
        assertNull(t1);

        Token t2 = null;
        try {
            t2 = processingToken.used(PAYMENT);
        } catch (TokenException e) {
            fail();
        }
        assertNotNull(t2);
        assertEquals(TokenStatus.USED, t2.getStatus());

        Token t3 = null;
        try {
            t3 = usedToken.used(PAYMENT);
            fail();
        } catch (TokenException e) {
        }
        assertNull(t3);

        Token t4 = null;
        try {
            t4 = failtToken.used(PAYMENT);
            fail();
        } catch (TokenException e) {
        }
        assertNull(t4);
    }

    @Test
    @DisplayName("各状態のトークンを失敗にする")
    public void fail() {
        Token unusedToken = new Token(SHOPPER_ID, TOKEN_ID, TokenStatus.UNUSED);
        Token processingToken = new Token(SHOPPER_ID, TOKEN_ID, TokenStatus.PROCESSING);
        Token usedToken = new Token(SHOPPER_ID, TOKEN_ID, TokenStatus.USED);
        Token failtToken = new Token(SHOPPER_ID, TOKEN_ID, TokenStatus.FAILED);

        Token t1 = unusedToken.failed();
        assertNotNull(t1);
        assertEquals(TokenStatus.FAILED, t1.getStatus());

        Token t2 = processingToken.failed();
        assertNotNull(t2);
        assertEquals(TokenStatus.FAILED, t2.getStatus());

        Token t3 = usedToken.failed();
        assertNotNull(t3);
        assertEquals(TokenStatus.FAILED, t3.getStatus());

        Token t4 = failtToken.failed();
        assertNotNull(t4);
        assertEquals(TokenStatus.FAILED, t4.getStatus());
    }
}
