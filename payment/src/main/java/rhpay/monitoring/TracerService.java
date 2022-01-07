package rhpay.monitoring;

import io.opentracing.Span;
import io.opentracing.Tracer;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class TracerService {

    @Inject
    Tracer tracer;

    public String traceRest() {
        if (tracer != null) {
            Span activeSpan = tracer.activeSpan();

            if (activeSpan == null) {
                Span builtSpan = tracer.buildSpan("REST").start();
                tracer.activateSpan(builtSpan);
                return builtSpan.context().toTraceId();

            } else {
                return activeSpan.context().toTraceId();
            }
        }
        return "";
    }

    public String traceRepository() {
        if (tracer != null) {
            Span builtSpan = tracer.buildSpan("Repository").start();
            tracer.activateSpan(builtSpan);
            return builtSpan.context().toTraceId();
        }
        return "";
    }

    public void closeTrace(){
        if(tracer != null){
            tracer.activeSpan().finish();
        }
    }
}
