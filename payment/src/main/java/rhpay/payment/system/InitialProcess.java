package rhpay.payment.system;

import io.quarkus.runtime.StartupEvent;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.commons.configuration.XMLStringConfiguration;
import rhpay.payment.cache.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.Set;

@ApplicationScoped
public class InitialProcess {

    @Inject
    RemoteCacheManager cacheManager;

    @Inject
    public InitialProcess(RemoteCacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    private static final String CACHE_CONFIG =
            "<distributed-cache name=\"%s\">"
                    + " <encoding media-type=\"application/x-protostream\"/>"
                    + " <groups enabled=\"true\"/>"
                    + "</distributed-cache>";

    void onStart(@Observes StartupEvent ev) {
        Set<String> cacheNames = cacheManager.getCacheNames();
        if (!cacheNames.contains("user")) {
            cacheManager.administration().getOrCreateCache("user", new XMLStringConfiguration(String.format(CACHE_CONFIG, "user")));
        }
        if (!cacheNames.contains("wallet")) {
            cacheManager.administration().getOrCreateCache("wallet", new XMLStringConfiguration(String.format(CACHE_CONFIG, "wallet")));
        }
        if (!cacheNames.contains("token")) {
            cacheManager.administration().getOrCreateCache("token", new XMLStringConfiguration(String.format(CACHE_CONFIG, "token")));
        }
        if (!cacheNames.contains("payment")) {
            cacheManager.administration().getOrCreateCache("payment", new XMLStringConfiguration(String.format(CACHE_CONFIG, "payment")));
        }
    }
}
