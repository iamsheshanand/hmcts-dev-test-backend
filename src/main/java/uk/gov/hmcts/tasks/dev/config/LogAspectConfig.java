package uk.gov.hmcts.tasks.dev.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Aspect
@Slf4j
public class LogAspectConfig {

    @Around("execution(public * uk.gov.hmcts.tasks.dev.controllers..*.*(..)) || "
            +
            "execution(public * uk.gov.hmcts.tasks.dev.services..*.*(..)) ")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs();

        log.info("Entering {}.{} with args {}", className, methodName, Arrays.toString(args));

        Object result;

        try {
            result = joinPoint.proceed(args);
            log.info("Exiting {}.{} with args {}", className, methodName,  Arrays.toString(args));
            return result;
        } catch (Throwable throwable) {
            log.error("Exception in method: {}.{}. Cause {}", className, methodName, throwable.getMessage()
            );
            throw throwable;
        }
    }
}
