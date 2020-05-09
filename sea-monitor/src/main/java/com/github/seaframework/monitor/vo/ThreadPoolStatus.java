package com.github.seaframework.monitor.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/4/9
 * @since 1.0
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ThreadPoolStatus {

    private String poolName;

    private int max;
    private int core;
    private int largest;
    private int active;
    private long task;
    private double activePercent;

    // unused
    private int queueSize;
}
