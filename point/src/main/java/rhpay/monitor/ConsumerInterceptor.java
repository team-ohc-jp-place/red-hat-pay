package rhpay.monitor;

import io.opentracing.Tracer;
import jdk.jfr.Event;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@MonitorConsumer
@Priority(1)
@Interceptor
public class ConsumerInterceptor {

    @Inject
    Tracer tracer;

    @AroundInvoke
    Object consumerInvocation(InvocationContext context) throws Exception {

        Event event = (tracer == null) ? new ConsumerEvent("", context.getMethod().getName()) : new ConsumerEvent(tracer.activeSpan().context().toTraceId(), context.getMethod().getName());
        event.begin();
        try {
            Object ret = context.proceed();
            return ret;
        } finally {
            event.commit();
        }
    }
}
