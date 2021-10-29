package rhpay.monitoring;

import io.opentracing.Tracer;
import jdk.jfr.Event;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@MonitorRest
@Priority(1)
@Interceptor
public class JfrInterceptor {

    @Inject
    Tracer tracer;

    @AroundInvoke
    Object jfrInvocation(InvocationContext context) throws Exception {

        Event event = (tracer == null) ? new RestEvent("", context.getMethod().getName()) : new RestEvent(tracer.activeSpan().context().toTraceId(), context.getMethod().getName());
        event.begin();
        try {
            Object ret = context.proceed();
            event.commit();
            return ret;
        } catch(Exception e){
            event.commit();
            throw e;
        }finally {

        }
    }
}
