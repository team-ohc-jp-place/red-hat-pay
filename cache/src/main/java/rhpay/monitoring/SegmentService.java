package rhpay.monitoring;

import org.infinispan.Cache;
import org.infinispan.distribution.LocalizedCacheTopology;

public class SegmentService {
    public static int getSegment(Cache cache, Object key){
        LocalizedCacheTopology cacheTopology = cache.getAdvancedCache().getDistributionManager().getCacheTopology();
        return cacheTopology.getSegment(key);
    }
}
