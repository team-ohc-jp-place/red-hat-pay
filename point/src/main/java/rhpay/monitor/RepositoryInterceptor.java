package rhpay.monitor;

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

        String traceId = (tracer == null || tracer.activeSpan() == null || tracer.activeSpan().context() == null || tracer.activeSpan().context().toTraceId() == null) ? "" : tracer.activeSpan().context().toTraceId();
        Event event = new RepositoryEvent(traceId, context.getMethod().getName());
        event.begin();
        try {
            Object ret = context.proceed();
            return ret;
        } finally {
            event.commit();
        }
    }

}
