package rhpay.payment.system;

import io.quarkus.runtime.StartupEvent;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.commons.configuration.XMLStringConfiguration;
import rhpay.payment.cache.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

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
        RemoteCache<ShopperKey, ShopperEntity> userCache = cacheManager.administration().getOrCreateCache("user", new XMLStringConfiguration(String.format(CACHE_CONFIG, "user")));
        userCache.put(new ShopperKey(1), new ShopperEntity("user1"));

        RemoteCache<ShopperKey, WalletEntity> walletCache = cacheManager.administration().getOrCreateCache("wallet", new XMLStringConfiguration(String.format(CACHE_CONFIG, "wallet")));
        walletCache.put(new ShopperKey(1), new WalletEntity(2000, 3000));

        RemoteCache<TokenKey, TokenEntity> tokenCache = cacheManager.administration().getOrCreateCache("token", new XMLStringConfiguration(String.format(CACHE_CONFIG, "token")));
        RemoteCache<TokenKey, PaymentEntity> paymentCache = cacheManager.administration().getOrCreateCache("payment", new XMLStringConfiguration(String.format(CACHE_CONFIG, "payment")));
    }
}
