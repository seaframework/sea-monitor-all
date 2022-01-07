package com.github.seaframework.monitor.message.disruptor;

import com.github.seaframework.core.config.Configuration;
import com.github.seaframework.core.config.ConfigurationFactory;
import com.github.seaframework.monitor.common.MonitorConst;
import com.github.seaframework.monitor.dto.MetricDTO;
import com.github.seaframework.monitor.message.MessageProducer;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.LiteTimeoutBlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/3/27
 * @since 1.0
 */
@Slf4j
public class DefaultMessageProducer implements MessageProducer {

    private volatile boolean init = false;
    private Disruptor<MessageEvent> disruptor;
    private RingBuffer<MessageEvent> ringBuffer;
    private int DEFAULT_BUFFER_SIZE = 4096;

    public void init() {
        if (init) {
            return;
        }
        //
        Configuration config = ConfigurationFactory.getInstance();
        int consumerCount = config.getInt(MonitorConst.CONFIG_KEY_CONSUMER_COUNT, MonitorConst.DEFAULT_CONSUMER_COUNT);

        ThreadFactory threadFactory = r -> new Thread(r, "sea-monitor-disruptor");

        MessageEventFactory factory = new MessageEventFactory();
        int bufferSize = DEFAULT_BUFFER_SIZE;

        disruptor = new Disruptor<>(factory, bufferSize, threadFactory, ProducerType.MULTI, new LiteTimeoutBlockingWaitStrategy(500, TimeUnit.MILLISECONDS));

        WorkHandler[] workHandlers = new WorkHandler[consumerCount];
        for (int i = 0; i < consumerCount; i++) {
            workHandlers[i] = new MessageConsumerHandler();
        }

        disruptor.handleEventsWithWorkerPool(workHandlers);

        disruptor.setDefaultExceptionHandler(new ExceptionHandler<MessageEvent>() {
            @Override
            public void handleEventException(Throwable ex, long sequence, MessageEvent event) {
                log.error("event exception =>,{},{},{}", ex, sequence, event);
            }

            @Override
            public void handleOnStartException(Throwable ex) {
                log.error("on start exception", ex);
            }

            @Override
            public void handleOnShutdownException(Throwable ex) {
                log.error("on shutdown exception", ex);
            }
        });

        disruptor.start();
        ringBuffer = disruptor.getRingBuffer();
        init = true;
    }

    private void checkInit() {
        init();
    }

    /**
     * 添加数据
     *
     * @param metricDTO
     */
    public void push(MetricDTO metricDTO) {

        try {
            checkInit();

            long sequence = ringBuffer.tryNext();
            try {
                MessageEvent event = ringBuffer.get(sequence);
                event.setMetricDTO(metricDTO);
            } finally {
                ringBuffer.publish(sequence);
            }
        } catch (Exception e) {
            log.error("fail to push metric", e);
        }
    }

    /**
     * 释放资源
     */
    public void shutdown() {
        disruptor.shutdown();
        init = false;
    }
}
