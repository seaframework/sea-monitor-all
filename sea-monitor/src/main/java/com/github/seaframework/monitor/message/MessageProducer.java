package com.github.seaframework.monitor.message;

import com.github.seaframework.monitor.dto.MetricDTO;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/3/27
 * @since 1.0
 */
public interface MessageProducer {

    void init();

    void push(MetricDTO dto);

    void shutdown();
}
