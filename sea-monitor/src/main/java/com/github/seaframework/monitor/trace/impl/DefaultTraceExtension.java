package com.github.seaframework.monitor.trace.impl;

import com.alipay.common.tracer.core.context.span.SofaTracerSpanContext;
import com.alipay.common.tracer.core.holder.SofaTraceContextHolder;
import com.github.seaframework.core.loader.LoadLevel;
import com.github.seaframework.monitor.trace.TraceExtension;
import lombok.extern.slf4j.Slf4j;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/5/9
 * @since 1.0
 */
@Slf4j
@LoadLevel(name = "default")
public class DefaultTraceExtension implements TraceExtension {

    @Override
    public String getTraceId() {
        try {
            SofaTracerSpanContext spanContext = SofaTraceContextHolder.getSofaTraceContext()
                                                                      .getCurrentSpan()
                                                                      .getSofaTracerSpanContext();
            return spanContext.getTraceId();
        } catch (Exception e) {
            log.error("fail to get span_id");
        }

        return "";
    }
}
