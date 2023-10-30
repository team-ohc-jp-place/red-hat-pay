package rhpay.payment.rest;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.exparity.hamcrest.date.LocalDateTimeMatchers;
import org.hamcrest.Matchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.modules.maven.ArtifactCoordinates;
import org.jboss.modules.maven.MavenResolver;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import rhpay.payment.di.TokenUsecaseMockProducer;
import rhpay.payment.mock.TokenUsecaseMock;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ExtendWith(ArquillianExtension.class)
public class PaymentResourceUTest {

    @ArquillianResource
    URL deploymentUrl;

    @Deployment
    public static WebArchive createDeployment() throws IOException {
        return ShrinkWrap.create(WebArchive.class)
                // ビジネスロジックをデプロイする
                .addAsLibraries(MavenResolver.createDefaultResolver().resolveJarArtifact(ArtifactCoordinates.fromString("rhpay:domain:1.0-SNAPSHOT")))
                // REST APIをデプロイする
                .addPackage(PaymentResource.class.getPackage())
                // ビジネスロジックは正常終了するモックを使用する
                .addClass(TokenUsecaseMock.class)
                // REST APIがモックのビジネスロジックを使用するように設定する
                .addClass(TokenUsecaseMockProducer.class);
    }

    @Test
    @DisplayName("支払い処理REST APIの単体テスト")
    @RunAsClient
    public void paymentRestTest1() throws Exception {

        int storeId = 1;
        int shopperId = 2;
        int billAmount = 3;
        String tokenId = "abc";
        LocalDateTime before = LocalDateTime.now();

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .post(new URL(deploymentUrl, createURL(shopperId, tokenId, storeId, billAmount)));
                /*
                返される JSON
                {
                    "billingAmount": 3,
                    "billingDateTime": "2023-04-05T06:07:08",
                    "shopperId": 2,
                    "storeId": 1,
                    "tokenId": "abc"
                }
                 */

        response
                .then()
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_OK)
                .body("billingAmount", is(3))
                .body("billingDateTime", Matchers.anything())
                .body("shopperId", is(2))
                .body("storeId", is(1))
                .body("tokenId", is("abc"));

        String billingDateTimeStr = response.path("billingDateTime");
        LocalDateTime billingDateTime = LocalDateTime.parse(billingDateTimeStr);
        assertThat(billingDateTime, LocalDateTimeMatchers.after(before));
        assertThat(billingDateTime, LocalDateTimeMatchers.before(LocalDateTime.now()));
    }

    /**
     * ワークショップ用のテスト
     *
     * @throws Exception
     */
    @Test
    @DisplayName("支払い処理REST APIの単体テスト for ワークショップ")
    @RunAsClient
    public void paymentRestTest2() throws Exception {

        int storeId = 2;
        int shopperId = 3;
        int billAmount = 4;
        String tokenId = "abcd";
        LocalDateTime before = LocalDateTime.now();

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .post(new URL(deploymentUrl, createURL(shopperId, tokenId, storeId, billAmount)));

                /*
                返される JSON
                {
                    "billingAmount": 4,
                    "billingDateTime": "2023-10-18T16:33:07.4407828",
                    "shopperId": 3,
                    "storeId": 2,
                    "tokenId": "abcd"
                }
                この JSON を参考に、以下の ? 部分を置き換えてください
                 */

/* この行を削除します
        response
                .then()
                .assertThat()
                 // HTTPのステータスコードを指定します
                .statusCode(HttpURLConnection.?)
                // 期待するストアのIDを指定します
                .body("storeId", is(?))
                // 期待する買い物客のIDを指定します
                .body("shopperId", is(?))
                // 期待するトークンIDを指定します
                .body("tokenId", is(?))
                // 期待する請求額を指定します
                .body("billingAmount", is(?));
この行を削除します */

        String billingDateTimeStr = response.path("billingDateTime");
        LocalDateTime billingDateTime = LocalDateTime.parse(billingDateTimeStr);
        assertThat(billingDateTime, LocalDateTimeMatchers.after(before));
        assertThat(billingDateTime, LocalDateTimeMatchers.before(LocalDateTime.now()));
    }

    private static String createURL(int shopperId, String tokenId, int storeId, int billAmount) {
        return String.format("api/pay/%d/%s/%d/%d", shopperId, tokenId, storeId, billAmount);
    }
}
