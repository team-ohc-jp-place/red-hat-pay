package rhpay.monitoring;

import jakarta.enterprise.context.RequestScoped;

import java.util.UUID;

@RequestScoped
public class TracerService {

    private static final boolean isDebug = false;

//    @Inject
//    Tracer tracer;

    public String traceRest() {

        if (!isDebug) {
            return "";
        }

        String traceId = null;
//        if (tracer != null) {
//            Span activeSpan = tracer.activeSpan();
//
//
//            if (activeSpan == null) {
//                Span builtSpan = tracer.buildSpan("REST").start();
//                tracer.activateSpan(builtSpan);
//                traceId = builtSpan.context().toTraceId();
//
//            } else {
//                traceId = activeSpan.context().toTraceId();
//            }
//        }

        if (traceId != null && !traceId.trim().equals("")) {
            return traceId;
        }
        return "REST:" + UUID.randomUUID();
    }

    public String traceRepository() {

        if (!isDebug) {
            return "";
        }

        String traceId = null;
//        if (tracer != null) {
//            Span activeSpan = tracer.activeSpan();
//
//
//            if (activeSpan == null) {
//                Span builtSpan = tracer.buildSpan("REST").start();
//                tracer.activateSpan(builtSpan);
//                traceId = builtSpan.context().toTraceId();
//
//            } else {
//                traceId = activeSpan.context().toTraceId();
//            }
//        }

        if (traceId != null && !traceId.trim().equals("")) {
            return traceId;
        }
        return "payment:" + UUID.randomUUID();

    }

    public void closeTrace() {

        if (!isDebug) {
            return;
        }

//        if (tracer != null) {
//            tracer.activeSpan().finish();
//        }
    }
}
