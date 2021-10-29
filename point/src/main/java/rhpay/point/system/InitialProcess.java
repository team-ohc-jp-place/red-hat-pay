package rhpay.point.system;

import rhpay.point.cache.PointEntity;
import rhpay.payment.cache.ShopperKey;
import io.quarkus.runtime.StartupEvent;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.commons.configuration.XMLStringConfiguration;

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
        RemoteCache<ShopperKey, PointEntity> pointCache = cacheManager.administration().getOrCreateCache("point", new XMLStringConfiguration(String.format(CACHE_CONFIG, "point")));
        pointCache.put(new ShopperKey(1), new PointEntity(10));
    }
}
