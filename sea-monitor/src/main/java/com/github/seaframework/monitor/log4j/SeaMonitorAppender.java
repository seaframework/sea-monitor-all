package com.github.seaframework.monitor.log4j;

import com.github.seaframework.monitor.SeaMonitor;
import com.github.seaframework.monitor.enums.CounterEnum;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

public class SeaMonitorAppender extends AppenderSkeleton {

    @Override
    protected void append(LoggingEvent event) {
        Level level = event.getLevel();

        if (level.isGreaterOrEqual(Level.ERROR)) {
            logError(event);
        }
    }

    @Override
    public void close() {
    }

    private void logError(LoggingEvent event) {
        SeaMonitor.logCount(CounterEnum.SYS_LOG_COUNT.getKey());
    }

    @Override
    public boolean requiresLayout() {
        return false;
    }
}
