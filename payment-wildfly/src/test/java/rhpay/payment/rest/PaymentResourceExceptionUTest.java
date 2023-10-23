package rhpay.payment.rest;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
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
import rhpay.payment.di.TokenUsecaseExceptionMockProducer;
import rhpay.payment.mock.TokenUsecaseExceptionMock;

import java.io.IOException;
import java.net.URL;

import static io.restassured.RestAssured.given;

@ExtendWith(ArquillianExtension.class)
public class PaymentResourceExceptionUTest {

    @ArquillianResource
    URL deploymentUrl;

    @Deployment
    public static WebArchive createDeployment() throws IOException {
        return ShrinkWrap.create(WebArchive.class)
                // ビジネスロジックをデプロイする
                .addAsLibraries(MavenResolver.createDefaultResolver().resolveJarArtifact(ArtifactCoordinates.fromString("rhpay:domain:1.0-SNAPSHOT")))
                // REST APIをデプロイする
                .addPackage(PaymentResource.class.getPackage())
                // ビジネスロジックは例外終了するモックを使用する
                .addClass(TokenUsecaseExceptionMock.class)
                // REST APIが例外終了するモックのビジネスロジックを使用するように設定する
                .addClass(TokenUsecaseExceptionMockProducer.class);
    }

    /**
     * ワークショップ用のテスト
     *
     * @throws Exception
     */
    @Test
    @DisplayName("支払い処理REST APIが例外発生した場合の単体テスト")
    @RunAsClient
    public void paymentExpectedExceptionTest() throws Exception {

        int storeId = 1;
        int shopperId = 2;
        int billAmount = 3;
        String tokenId = "abc";

        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .post(new URL(deploymentUrl, createURL(shopperId, tokenId, storeId, billAmount)));

                /*
                例外が発生するため、JSONは返されません。
                Javaの例外はHTTPではInternal Server Errorとして返されます。
                HTTPのステータスコードはいくつになるでしょうか？
                また、ステータスコードが定義されているHttpURLConnectionの定数は何になるでしょうか？
                参考：https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/net/HttpURLConnection.html
                以下の ? 部分を置き換えてください。
                 */

/* この行を削除します
        response
                .then()
                .assertThat()
                .statusCode(HttpURLConnection.?);
この行を削除します */
    }

    private static String createURL(int shopperId, String tokenId, int storeId, int billAmount) {
        return String.format("api/pay/%d/%s/%d/%d", shopperId, tokenId, storeId, billAmount);
    }
}
