package com.github.seaframework.monitor.enums;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/3/25
 * @since 1.0
 */
public enum MonitorModeEnum {
    REPORT,
    LOG;


    public static MonitorModeEnum of(String value) {
        MonitorModeEnum[] values = values();

        for (int i = 0; i < values.length; i++) {
            MonitorModeEnum item = values[i];
            if (item.ordinal() == Integer.valueOf(value).intValue()) {
                return item;
            }
        }
        return null;
    }
}
