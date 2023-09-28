package rhpay.payment.rest;

import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.modules.maven.ArtifactCoordinates;
import org.jboss.modules.maven.MavenResolver;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.jboss.arquillian.junit5.ArquillianExtension;
import rhpay.payment.di.TokenUsecaseMockProducer;

import java.io.IOException;
import java.net.URL;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@ExtendWith(ArquillianExtension.class)
public class PaymentResourceUnitTest {

    @ArquillianResource
    URL deploymentUrl;

    @Deployment
    public static WebArchive createDeployment() throws IOException {
        return ShrinkWrap.create(WebArchive.class)
                .addAsLibraries(MavenResolver.createDefaultResolver().resolveJarArtifact(ArtifactCoordinates.fromString("rhpay:domain:1.0-SNAPSHOT")))
                .addPackage(HelloResource.class.getPackage())
                .addPackage("rhpay.payment.repository")
                .addPackage("rhpay.payment.mock")
                .addClass(TokenUsecaseMockProducer.class);
    }

    @Test
    @RunAsClient
    public void jaxrsResourceTest() throws Exception{

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .post(new URL(deploymentUrl, "api/pay/1/1/1/10"))
                .then()
                .assertThat()
                .statusCode(200)
                .body("storeId", is(1))
                .body("shopperId", is(2))
                .body("tokenId", is("abc"))
                .body("billingAmount", is(3))
                .body("billingDateTime", Matchers.anything());
    }
}
