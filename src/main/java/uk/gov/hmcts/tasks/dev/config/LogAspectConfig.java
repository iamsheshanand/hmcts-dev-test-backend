package uk.gov.hmcts.tasks.dev.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class LogAspectConfig {

    @Before("execution(* uk.gov.hmcts.tasks.dev..*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Before {}", joinPoint.getSignature().getName());
    }

    @After("execution(* uk.gov.hmcts.tasks.dev..*.*(..))")
    public void logAfter(JoinPoint joinPoint) {
        log.info("After {}", joinPoint.getSignature().getName());
    }

}
