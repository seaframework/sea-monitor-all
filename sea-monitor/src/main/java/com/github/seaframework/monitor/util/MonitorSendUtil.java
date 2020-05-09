package com.github.seaframework.monitor.util;

import com.github.seaframework.core.http.simple.HttpClientUtil;
import com.github.seaframework.core.model.BaseResult;
import lombok.extern.slf4j.Slf4j;

/**
 * module name
 *
 * @author spy
 * @version 1.0 2020/3/27
 * @since 1.0
 */
@Slf4j
public class MonitorSendUtil {
    public void send(String url, Object obj) {
        BaseResult ret = HttpClientUtil.postJSONSafe(url, obj);
        if (!ret.getSuccess()) {
            log.warn("sea monitor send error.");
        }
    }
}
