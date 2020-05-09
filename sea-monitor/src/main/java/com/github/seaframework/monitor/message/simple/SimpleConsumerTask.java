package com.github.seaframework.monitor.message.simple;

import com.github.seaframework.core.config.Configuration;
import com.github.seaframework.core.config.ConfigurationFactory;
import com.github.seaframework.core.http.simple.HttpClientUtil;
import com.github.seaframework.core.model.BaseResult;
import com.github.seaframework.core.util.JSONUtil;
import com.github.seaframework.core.util.ListUtil;
import com.github.seaframework.monitor.common.MonitorConst;
import com.github.seaframework.monitor.dto.MetricDTO;
import com.google.common.collect.Queues;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/3/27
 * @since 1.0
 */
@Slf4j
public class SimpleConsumerTask implements Runnable {
    private volatile boolean isRunning = true;
    private BlockingQueue queue;


    public SimpleConsumerTask(BlockingQueue queue) {
        this.queue = queue;
    }

    /**
     * 消费者
     */
    public void run() {
        Configuration config = ConfigurationFactory.getInstance();
        int maxElementCount = config.getInt(MonitorConst.CONFIG_KEY_SEND_ELEMENT_MAX_COUNT, MonitorConst.DEFAULT_SEND_ELEMENT_MAX_COUNT);
        int timeout = config.getInt(MonitorConst.CONFIG_KEY_SEND_PERIOD_TIME, MonitorConst.DEFAULT_PUSH_PERIOD_TIME);

        while (isRunning) {
            try {
                List<MetricDTO> metricDTOList = new ArrayList<>();
                // 满足200元素 或10s 即可提交
                Queues.drain(queue, metricDTOList, maxElementCount, timeout, TimeUnit.SECONDS);
                send(metricDTOList);
                Thread.sleep(200);
            } catch (Exception e) {
                log.error(" drain queues error:", e);
            }
        }
    }

    public void stop() {
        isRunning = false;
    }

    private void send(List<MetricDTO> metricDTOList) {
        if (ListUtil.isEmpty(metricDTOList)) {
            return;
        }
        String url = ConfigurationFactory.getInstance().getString(MonitorConst.CONFIG_KEY_URI, MonitorConst.DEFAULT_COLLECTOR_URI);
        BaseResult ret = HttpClientUtil.postJSONSafe(url, JSONUtil.toStr(metricDTOList));
        log.info("post to sea monitor server [count={},success={}]", metricDTOList.size(), ret.getSuccess());
    }

}
