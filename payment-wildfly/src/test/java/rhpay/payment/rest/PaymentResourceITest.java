package rhpay.payment.rest;

import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.modules.maven.ArtifactCoordinates;
import org.jboss.modules.maven.MavenResolver;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import rhpay.payment.di.TokenUsecaseProducer;

import java.io.IOException;
import java.net.URL;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@ExtendWith(ArquillianExtension.class)
public class PaymentResourceITest {

    @ArquillianResource
    URL deploymentUrl;

    @Deployment
    public static WebArchive createDeployment() throws IOException {
        return ShrinkWrap.create(WebArchive.class)
                .addAsLibraries(MavenResolver.createDefaultResolver().resolveJarArtifact(ArtifactCoordinates.fromString("rhpay:domain:1.0-SNAPSHOT")))
                .addPackage(PaymentResource.class.getPackage())
                .addPackage("rhpay.payment.rest")
                // TODO 現時点では固定のオブジェクトを返すリポジトリなので、将来的にDBを使うようにする
                .addPackage("rhpay.payment.repository")
                .addClass(TokenUsecaseProducer.class);
    }

    @Test
    @RunAsClient
    public void jaxrsResourceTest() throws Exception {

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .post(new URL(deploymentUrl, "api/pay/1/1/1/10"))
                .then()
                .assertThat()
                .statusCode(200)
                .body("storeId", is(1))
                .body("shopperId", is(1))
                .body("tokenId", is("1"))
                .body("billingAmount", is(10))
                .body("billingDateTime", Matchers.anything());
    }
}