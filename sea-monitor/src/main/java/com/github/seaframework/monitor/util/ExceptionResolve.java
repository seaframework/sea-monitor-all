package com.github.seaframework.monitor.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.web.util.NestedServletException;

import java.lang.reflect.InvocationTargetException;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/10/20
 * @since 1.0
 */
@Slf4j
public class ExceptionResolve {

    /**
     * 获取最简单的异常信息，目标异常前两行
     *
     * @param t
     * @return
     */
    public static String getSimpleMsg(Throwable t) {
        if (t == null) {
            return "";
        }
        String shortMsg = "";

        if (t instanceof NestedServletException) {
            NestedServletException e = (NestedServletException) t;

            if (e.getCause() != null) {
                if (e.getCause() instanceof InvocationTargetException) {
                    shortMsg = checkInvocationTargetException(e.getCause());
                } else {
                    shortMsg = getShortMsg(e.getCause());
                }
            } else {
                shortMsg = getShortMsg(e);
            }
        } else if (t instanceof InvocationTargetException) {
            InvocationTargetException invocationTargetException = (InvocationTargetException) t;
            Throwable target = invocationTargetException.getTargetException();
            if (target != null) {
                shortMsg = getShortMsg(target);
            } else {
                log.warn("invocation target exception target is null");
                shortMsg = getShortMsg(t);
            }
        } else {
            shortMsg = getShortMsg(t);
        }

        return shortMsg;
    }

    private static String getShortMsg(Throwable t) {
        if (t == null) {
            return StringUtils.EMPTY;
        }
        String shortMsg = StringUtils.EMPTY;

        try {
            String stackMsg = ExceptionUtils.getStackTrace(t);
            stackMsg = StringUtils.defaultIfEmpty(stackMsg, StringUtils.EMPTY);

            String[] msgArray = stackMsg.split(System.lineSeparator(), 3);
            if (msgArray.length == 1) {
                shortMsg = msgArray[0];
            } else if (msgArray.length >= 2) {
                shortMsg = msgArray[0] + "\n" + msgArray[1];
            }
        } catch (Exception e) {
            log.error("fail to get short msg of stack exception.");
        }

        return shortMsg;
    }

    private static String checkInvocationTargetException(Throwable t) {
        String shortMsg = StringUtils.EMPTY;
        if (t == null) {
            return shortMsg;
        }

        if (t instanceof InvocationTargetException) {
            InvocationTargetException invocationTargetException = (InvocationTargetException) t;
            Throwable target = invocationTargetException.getTargetException();
            if (target != null) {
                shortMsg = getShortMsg(target);
            } else {
                log.warn("invocation target exception target is null");
            }
        }

        return shortMsg;
    }

}
