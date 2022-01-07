package com.github.seaframework.monitor.plugin.aop;

import com.github.seaframework.core.util.StringUtil;
import com.github.seaframework.monitor.SeaMonitor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;

import java.util.Objects;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/4/30
 * @since 1.0
 */
@Slf4j
@Aspect
@Order(100)
public class SeaMonitorAspect {

    @Around(value = "@annotation(annotation)", argNames = "joinPoint,annotation")
    public Object doAround(ProceedingJoinPoint joinPoint, SeaMonitorTrace annotation) throws Throwable {
        if (log.isDebugEnabled()) {
            log.debug("sea monitor aspect begin.");
        }

        if (Objects.isNull(annotation) || StringUtil.isEmpty(annotation.metric())) {
            return joinPoint.proceed();
        }

        try {
            Object proceed = joinPoint.proceed();
            return proceed;
        } catch (Throwable throwable) {
            log.warn("exception500");
            SeaMonitor.logErrorCount(annotation.metric());
            throw throwable;
        }
    }
}
