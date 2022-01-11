package rhpay.monitoring;

import jdk.jfr.Event;
import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.TransactionCompleted;
import org.infinispan.notifications.cachelistener.annotation.TransactionRegistered;
import org.infinispan.notifications.cachelistener.event.TransactionCompletedEvent;
import org.infinispan.notifications.cachelistener.event.TransactionRegisteredEvent;
import org.infinispan.transaction.xa.GlobalTransaction;
import rhpay.monitoring.event.TransactionCompletedJfrEvent;
import rhpay.monitoring.event.TransactionRegisteredJfrEvent;

@Listener
public class TransactionListener {

    private static TransactionListener instance = new TransactionListener();

    public static TransactionListener getInstance() {
        return instance;
    }

    @TransactionCompleted
    public void transactionCompleted(TransactionCompletedEvent event) {

        String cacheName = event.getCache().getName();
        boolean originLocal = event.isOriginLocal();
        boolean transactionSuccessful = event.isTransactionSuccessful();
        String typeName = event.getType().name();
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

        jfrEvent = new TransactionCompletedJfrEvent(cacheName, originLocal, typeName, pre, globalId, internalId, remote, transactionSuccessful);
        jfrEvent.commit();
    }

    @TransactionRegistered
    public void transactionRegistered(TransactionRegisteredEvent event) {

        String cacheName = event.getCache().getName();
        boolean originLocal = event.isOriginLocal();
        String typeName = event.getType().name();
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

        jfrEvent = new TransactionRegisteredJfrEvent(cacheName, originLocal, typeName, pre, globalId, internalId, remote);
        jfrEvent.commit();
    }
}
