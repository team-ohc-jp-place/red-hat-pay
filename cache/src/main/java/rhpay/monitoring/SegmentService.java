package rhpay.monitoring;

import org.infinispan.Cache;
import org.infinispan.distribution.LocalizedCacheTopology;

public class SegmentService {
    private static final boolean isDebug = false;
    public static int getSegment(Cache cache, Object key){
        if(isDebug) {
            LocalizedCacheTopology cacheTopology = cache.getAdvancedCache().getDistributionManager().getCacheTopology();
            return cacheTopology.getSegment(key);
        }
        return -1;
    }
}
