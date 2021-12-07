package rhpay.monolith.monitoring;

import jdk.jfr.Event;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class RestInterceptor {

    @Before("execution(* rhpay.monolith.rest.*.*(..))")
    public void invokeBefore(JoinPoint joinPoint){
    }

    @Around("execution(* rhpay.monolith.rest.*.*(..))")
    public Object invoke(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object ret = null;
        Event event = new RestEvent(proceedingJoinPoint.getTarget().toString(), proceedingJoinPoint.getSignature().getName());

        try{

            event.begin();

            ret = proceedingJoinPoint.proceed();

            return ret;

        }finally{
            event.commit();
        }
    }
}
