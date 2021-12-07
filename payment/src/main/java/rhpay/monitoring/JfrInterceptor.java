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

        String traceId = (tracer == null) ? "" : tracer.activeSpan().context().toTraceId();
        
        Event event =  new RestEvent(traceId, context.getTarget().toString(), context.getMethod().getName());
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
