package com.github.seaframework.monitor.message.disruptor;

import cn.hutool.core.util.RandomUtil;
import com.github.seaframework.monitor.dto.MetricDTO;
import com.github.seaframework.monitor.message.simple.SimpleConsumerTask;
import com.google.common.collect.Queues;
import com.lmax.disruptor.WorkHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/3/27
 * @since 1.0
 */
@Slf4j
public class MessageConsumerHandler implements WorkHandler<MessageEvent> {

    private boolean init = false;
    private int DEFAULT_QUEUE_SIZE = 5000;
    private BlockingQueue<MetricDTO> queue;
    private static final AtomicLong count = new AtomicLong(0);


    @Override
    public void onEvent(MessageEvent event) throws Exception {

        if (!init) {
            initQueue();
        }
        queue.put(event.getMetricDTO());
    }


    private void initQueue() {
        if (queue == null) {
            queue = Queues.newArrayBlockingQueue(DEFAULT_QUEUE_SIZE);
        }
        try {
            TimeUnit.MILLISECONDS.sleep(RandomUtil.randomInt(100, 500));
        } catch (Exception e) {
            log.error("--", e);
        }
        SimpleConsumerTask task = new SimpleConsumerTask(queue);
        Thread thread = new Thread(task);
        thread.setName("sea-monitor-message-consumer-" + count.incrementAndGet());
        thread.start();

        Thread shutdownThread = new Thread(() -> task.stop());
        shutdownThread.setDaemon(true);
        Runtime.getRuntime().addShutdownHook(shutdownThread);

        init = true;
    }
}
