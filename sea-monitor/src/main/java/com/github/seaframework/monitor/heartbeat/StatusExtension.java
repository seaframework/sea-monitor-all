
package com.github.seaframework.monitor.heartbeat;

import java.util.Map;

/**
 * 扩展接口
 */
public interface StatusExtension {

    /**
     * 描述
     *
     * @return
     */
    String getDescription();

    /**
     * 唯一标识
     *
     * @return
     */
    String getId();

    /**
     * 返回指标点
     *
     * @return
     */
    Map<String, Object> getProperties();
}
