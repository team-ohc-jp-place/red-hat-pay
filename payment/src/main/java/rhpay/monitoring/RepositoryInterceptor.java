package rhpay.monitoring;

import io.opentracing.Tracer;
import jdk.jfr.Event;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@MonitorRepository
@Priority(1)
@Interceptor
public class RepositoryInterceptor {

    @Inject
    Tracer tracer;

    @AroundInvoke
    Object repositoryInvocation(InvocationContext context) throws Exception {

        Event event = (tracer == null) ? new RepositoryEvent("", context.getMethod().getName()) : new RepositoryEvent(tracer.activeSpan().context().toTraceId(), context.getMethod().getName());
        event.begin();
        try {
            Object ret = context.proceed();
            return ret;
        } finally {
            event.commit();
        }
    }

}
