package com.github.seaframework.monitor.annotation;

import java.lang.annotation.*;

/**
 * monitor for method.
 *
 * @author spy
 * @version 1.0 2020/4/30
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface SeaMonitorTrace {

    String metric() default "";

}
