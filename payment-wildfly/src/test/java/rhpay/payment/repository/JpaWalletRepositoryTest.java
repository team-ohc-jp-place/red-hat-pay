package rhpay.payment.repository;

import jakarta.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.modules.maven.ArtifactCoordinates;
import org.jboss.modules.maven.MavenResolver;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import rhpay.payment.domain.FullName;
import rhpay.payment.domain.Shopper;
import rhpay.payment.domain.ShopperId;
import rhpay.payment.domain.Wallet;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ArquillianExtension.class)
public class JpaWalletRepositoryTest {

    @Deployment
    public static WebArchive createDeployment() throws IOException {
        return ShrinkWrap.create(WebArchive.class)
                .addAsLibraries(MavenResolver.createDefaultResolver().resolveJarArtifact(ArtifactCoordinates.fromString("rhpay:domain:1.0-SNAPSHOT")))
                .addPackage("rhpay.payment.repository");
    }

    @Inject
    WalletRepository repository;

    Shopper SHOPPER = new Shopper(new ShopperId(1), new FullName("abc"));

    @Test
    public void test(){
        Wallet w = repository.load(SHOPPER);

        assertEquals(1, w.getOwner().getId().value);
        assertEquals("abc", w.getOwner().getUserName().value);
    }
}
