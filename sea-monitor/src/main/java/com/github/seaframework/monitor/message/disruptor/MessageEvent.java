package com.github.seaframework.monitor.message.disruptor;

import com.github.seaframework.monitor.dto.MetricDTO;
import lombok.Data;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/3/27
 * @since 1.0
 */

@Data
public class MessageEvent {

    private MetricDTO metricDTO;

}
