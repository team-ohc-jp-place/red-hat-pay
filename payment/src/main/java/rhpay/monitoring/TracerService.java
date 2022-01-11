package rhpay.monitoring;

import io.opentracing.Span;
import io.opentracing.Tracer;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.UUID;

@RequestScoped
public class TracerService {

    @Inject
    Tracer tracer;

    public String traceRest() {

        String traceId = null;
        if (tracer != null) {
            Span activeSpan = tracer.activeSpan();


            if (activeSpan == null) {
                Span builtSpan = tracer.buildSpan("REST").start();
                tracer.activateSpan(builtSpan);
                traceId = builtSpan.context().toTraceId();

            } else {
                traceId = activeSpan.context().toTraceId();
            }
        }

        if (traceId != null && !traceId.trim().equals("")) {
            return traceId;
        }
        return "REST:" + UUID.randomUUID();
    }

    public String traceRepository() {
        String traceId = null;
        if (tracer != null) {
            Span activeSpan = tracer.activeSpan();


            if (activeSpan == null) {
                Span builtSpan = tracer.buildSpan("REST").start();
                tracer.activateSpan(builtSpan);
                traceId = builtSpan.context().toTraceId();

            } else {
                traceId = activeSpan.context().toTraceId();
            }
        }

        if (traceId != null && !traceId.trim().equals("")) {
            return traceId;
        }
        return "payment:" + UUID.randomUUID();

    }

    public void closeTrace() {
        if (tracer != null) {
            tracer.activeSpan().finish();
        }
    }
}
