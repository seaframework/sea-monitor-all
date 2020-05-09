package com.github.seaframework.monitor.util;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.Set;

public class JMXUtil {

    /**
     * 注册一个MBean
     */
    public static ObjectName register(String name, Object mbean) {
        try {
            ObjectName objectName = new ObjectName(name);
            MBeanServer mbeanServer = getMBeanServer();
            try {
                mbeanServer.registerMBean(mbean, objectName);
            } catch (InstanceAlreadyExistsException ex) {
                mbeanServer.unregisterMBean(objectName);
                mbeanServer.registerMBean(mbean, objectName);
            }
            return objectName;
        } catch (JMException e) {
            throw new IllegalArgumentException(name, e);
        }
    }

    /**
     * 取消一个MBean
     */
    public static void unregister(String name) {
        try {
            MBeanServer mbeanServer = getMBeanServer();
            mbeanServer.unregisterMBean(new ObjectName(name));
        } catch (JMException e) {
            throw new IllegalArgumentException(name, e);
        }
    }

    /**
     * 生成一个ObjectName，如果出错，返回null
     */
    public static ObjectName createObjectName(String pattern) {
        try {
            return new ObjectName(pattern);
        } catch (Exception ex) {
            // Ignore.
        }

        return null;
    }

    /**
     * 获取类型Pattern列表
     */
    public static ObjectName[] getObjectNames(ObjectName pattern) {
        ObjectName[] result = new ObjectName[0];
        Set<ObjectName> objectNames = getMBeanServer().queryNames(pattern, null);
        if (objectNames != null && !objectNames.isEmpty()) {
            result = objectNames.toArray(new ObjectName[objectNames.size()]);
        }
        return result;
    }

    public static MBeanServer getMBeanServer() {
        MBeanServer mBeanServer = null;
        if (MBeanServerFactory.findMBeanServer(null).size() > 0) {
            mBeanServer = MBeanServerFactory.findMBeanServer(null).get(0);
        } else {
            mBeanServer = ManagementFactory.getPlatformMBeanServer();
        }
        return mBeanServer;
    }
}
