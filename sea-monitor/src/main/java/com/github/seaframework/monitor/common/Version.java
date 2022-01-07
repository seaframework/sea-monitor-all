package com.github.seaframework.monitor.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/11/12
 * @since 1.0
 */
@Slf4j
public class Version {

    private static final String VERSION = getVersion("0.0.0");

    public static final String getVersion() {
        return VERSION;
    }

    public static String getVersion(String defaultVersion) {
        try {

            Class<?> clazz = Class.forName("com.github.spy.sea.monitor.common.BuildInfo");
            Object obj = FieldUtils.readStaticField(clazz, "VERSION", true);
            return obj == null ? defaultVersion : obj.toString();
        } catch (Exception e) {
            return defaultVersion;
        }
    }

}
