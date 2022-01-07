package com.github.seaframework.monitor.util;

import lombok.extern.slf4j.Slf4j;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2022/1/7
 * @since 1.0
 */
@Slf4j
public class MonitorUtil {

    public static String replace(final String value) {
        if (value == null || value.trim().isEmpty()) {
            return value;
        }
        if (value.contains(".")) {
            return value.replaceAll("\\.", "_");
        }
        return value;
    }
}
