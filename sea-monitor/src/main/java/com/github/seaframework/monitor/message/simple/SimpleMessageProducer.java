package com.github.seaframework.monitor.message.simple;

import com.github.seaframework.monitor.dto.MetricDTO;
import com.github.seaframework.monitor.message.MessageProducer;
import com.google.common.collect.Queues;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/3/27
 * @since 1.0
 */
@Slf4j
public class SimpleMessageProducer implements MessageProducer {

    private BlockingQueue queue;
    private int DEFAULT_QUEUE_SIZE = 5000;

    @Override
    public void init() {

        queue = Queues.newArrayBlockingQueue(DEFAULT_QUEUE_SIZE);

        Thread consumeThread = new Thread(new SimpleConsumerTask(queue));
        consumeThread.setName("sea-monitor-simple-message-consumer");
        consumeThread.start();

    }

    @Override
    public void push(MetricDTO dto) {
        try {
            if (queue.size() >= DEFAULT_QUEUE_SIZE) {
                log.warn("queue has reach to MAX SIZE[{}]", DEFAULT_QUEUE_SIZE);
                return;
            }
            queue.offer(dto);
        } catch (Exception e) {
            log.error("fail to push metric dto", e);
        }
    }

    @Override
    public void shutdown() {
    }
}
