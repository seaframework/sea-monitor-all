# JVM监控
  
## 指标

|指标|描述|
|----|----|
|jvm.classloading.loaded.count| 当前类加载数|
|jvm.classloading.totalloaded.count| 类加载总数 |
|jvm.classloading.unloaded.count| 类卸载总数|
|jvm.fullgc.count| fullgc次数 |
|jvm.fullgc.time| fullgc时间|
|jvm.gc.count| gc次数 |
|jvm.gc.time|gc时间 |
|jvm.memory.codecache.used|codecache使用内存 |
|jvm.memory.codecache.used.percent| codecache内存使用率|
|jvm.memory.eden.used| eden内存大小 |
|jvm.memory.eden.used.percent|eden内存使用率 |
|jvm.memory.metaspace.used| metaspace 内存大小 |
|jvm.memory.metaspace.used.percent| metaspace使用率 |
|jvm.memory.nonheap.used| 非堆内存大小 |
|jvm.memory.nonheap.used.percent|非堆内存使用率 |
|jvm.memory.oldgen.used| 老年代内存大小 |
|jvm.memory.oldgen.used.percent| 老年代内存使用率 |
|jvm.memory.oldgen.used.percent.after.fullgc| fullgc后老年代内存使用率 |
|jvm.memory.perm.used| perm内存大小|
|jvm.memory.perm.used.percent| perm内存使用率 |
|jvm.memory.survivor.used| survivor内存大小 |
|jvm.memory.survivor.used.percent| survivor内存使用率 |
|jvm.memory.used| jvm内存使用大小 |
|jvm.memory.used.percent| jvm内存使用率 |
|jvm.nio.directbuffer.used| nio directbuffer使用内存大小 |
|jvm.nio.mapped.used| nio mapped使用内存大小 |
|jvm.thread.blocked.count| blocked线程数 |
|jvm.thread.count| jvm中线程数 |
|jvm.thread.daemon.count| jvm中daemon线程数|
|jvm.thread.deadlock.count| jvm中死锁线程数 |
|jvm.thread.http.count| jvm中http线程数 |
|jvm.thread.new.count| jvm新建线程数|
|jvm.thread.runnable.count| jvm中运行中的线程数 |
|jvm.thread.terminated.count| 结束的线程数|
|jvm.thread.time_waiting.count| time_waiting状态的线程数|
|jvm.thread.totalstarted.count| jvm总共启动线程数 |
|jvm.thread.waiting.count| wating状态的线程数 |
|jvm.younggc.count| younggc次数|
|jvm.younggc.meantime| younggc持续时间|
|jvm.younggc.time| younggc时间|

## 配置

  无需配置,自动探测
