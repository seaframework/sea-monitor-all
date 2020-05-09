package com.github.seaframework.monitor.message.disruptor;

import com.lmax.disruptor.EventFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * 模块名
 *
 * @author shi.pengyan
 * @date 2019-03-27 9:37
 */
@Slf4j
public class MessageEventFactory implements EventFactory<MessageEvent> {
    @Override
    public MessageEvent newInstance() {
        return new MessageEvent();
    }
}
