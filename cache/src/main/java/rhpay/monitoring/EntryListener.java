package rhpay.monitoring;

import jdk.jfr.Event;
import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryVisited;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryVisitedEvent;
import org.infinispan.transaction.xa.GlobalTransaction;
import rhpay.monitoring.event.*;
import rhpay.payment.cache.ShopperKey;
import rhpay.payment.cache.TokenKey;

@Listener
public class EntryListener {

    private static EntryListener instance = new EntryListener();

    public static EntryListener getInstance(){
        return instance;
    }

    @CacheEntryCreated
    public void created(CacheEntryCreatedEvent event) {
        Object key = event.getKey();
        boolean hasValue = event.getValue() != null;

        String cacheName = event.getCache().getName();
        boolean originLocal = event.isOriginLocal();
        String name = event.getType().name();
        boolean pre = event.isPre();

        GlobalTransaction globalTransaction = event.getGlobalTransaction();

        Event jfrEvent = null;
        String globalId = null;
        long internalId = 0;
        boolean remote = false;

        if (globalTransaction != null) {
            globalId = globalTransaction.globalId();
            internalId = globalTransaction.getInternalId();
            remote = globalTransaction.isRemote();
        }

        if (key instanceof ShopperKey) {
            int shopperId = ((ShopperKey) key).getOwnerId();
            jfrEvent = new ShopperPutJfrEvent(cacheName, originLocal, name, pre, globalId, internalId, remote, hasValue, shopperId);
        } else if (key instanceof TokenKey) {
            int shopperId = ((TokenKey) key).getOwnerId();
            String tokenId = ((TokenKey) key).getTokenId();
            jfrEvent = new TokenPutJfrEvent(cacheName, originLocal, name, pre, globalId, internalId, remote, hasValue, shopperId, tokenId);
        } else {
            jfrEvent = new EntryJfrEvent(cacheName, originLocal, name, pre, globalId, internalId, remote, hasValue);
        }

        jfrEvent.commit();
    }

    @CacheEntryModified
    public void modified(CacheEntryModifiedEvent event) {
        Object key = event.getKey();
        boolean hasValue = event.getValue() != null;

        String cacheName = event.getCache().getName();
        boolean originLocal = event.isOriginLocal();
        String name = event.getType().name();
        boolean pre = event.isPre();

        GlobalTransaction globalTransaction = event.getGlobalTransaction();

        Event jfrEvent = null;
        String globalId = null;
        long internalId = 0;
        boolean remote = false;

        if (globalTransaction != null) {
            globalId = globalTransaction.globalId();
            internalId = globalTransaction.getInternalId();
            remote = globalTransaction.isRemote();
        }

        if (key instanceof ShopperKey) {
            int shopperId = ((ShopperKey) key).getOwnerId();
            jfrEvent = new ShopperPutJfrEvent(cacheName, originLocal, name, pre, globalId, internalId, remote, hasValue, shopperId);
        } else if (key instanceof TokenKey) {
            int shopperId = ((TokenKey) key).getOwnerId();
            String tokenId = ((TokenKey) key).getTokenId();
            jfrEvent = new TokenPutJfrEvent(cacheName, originLocal, name, pre, globalId, internalId, remote, hasValue, shopperId, tokenId);
        } else {
            jfrEvent = new EntryJfrEvent(cacheName, originLocal, name, pre, globalId, internalId, remote, hasValue);
        }

        jfrEvent.commit();
    }

    @CacheEntryVisited
    public void visited(CacheEntryVisitedEvent event) {
        Object key = event.getKey();
        boolean hasValue = event.getValue() != null;

        String cacheName = event.getCache().getName();
        boolean originLocal = event.isOriginLocal();
        String name = event.getType().name();
        boolean pre = event.isPre();

        GlobalTransaction globalTransaction = event.getGlobalTransaction();

        Event jfrEvent = null;
        String globalId = null;
        long internalId = 0;
        boolean remote = false;

        if (globalTransaction != null) {
            globalId = globalTransaction.globalId();
            internalId = globalTransaction.getInternalId();
            remote = globalTransaction.isRemote();
        }

        if (key instanceof ShopperKey) {
            int shopperId = ((ShopperKey) key).getOwnerId();
            jfrEvent = new ShopperGetJfrEvent(cacheName, originLocal, name, pre, globalId, internalId, remote, hasValue, shopperId);
        } else if (key instanceof TokenKey) {
            int shopperId = ((TokenKey) key).getOwnerId();
            String tokenId = ((TokenKey) key).getTokenId();
            jfrEvent = new TokenGetJfrEvent(cacheName, originLocal, name, pre, globalId, internalId, remote, hasValue, shopperId, tokenId);
        } else {
            jfrEvent = new EntryJfrEvent(cacheName, originLocal, name, pre, globalId, internalId, remote, hasValue);
        }

        jfrEvent.commit();

    }
}
