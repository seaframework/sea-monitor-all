package com.github.seaframework.monitor.plugin.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.LogbackException;
import com.github.seaframework.monitor.SeaMonitor;
import com.github.seaframework.monitor.enums.CounterEnum;
import lombok.extern.slf4j.Slf4j;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/5/3
 * @since 1.0
 */
@Slf4j
public class SeaMonitorAppender extends AppenderBase<ILoggingEvent> {

    @Override
    protected void append(ILoggingEvent event) {
        try {
            Level level = event.getLevel();

            if (level.isGreaterOrEqual(Level.ERROR)) {
                logError(event);
            }
        } catch (Exception ex) {
            throw new LogbackException(event.getFormattedMessage(), ex);
        }
    }

    private void logError(ILoggingEvent event) {
        SeaMonitor.logCount(CounterEnum.SYS_LOG_COUNT.getKey());
    }

}
