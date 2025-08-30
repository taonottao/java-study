package com.example.util;

import cn.hutool.core.date.StopWatch;
import java.util.concurrent.TimeUnit;

/**
 * 轻量计时工具封装：基于 Hutool StopWatch。
 * 用于与 JMH 形成对照（小规模、功能性验证时可使用）。
 */
public final class StopWatchUtil {

    private StopWatchUtil() {}

    /**
     * 运行一个任务并返回毫秒耗时。
     * @param task 可执行任务
     * @return 耗时毫秒
     */
    public static long runAndMeasureMillis(Runnable task) {
        StopWatch sw = new StopWatch();
        sw.start();
        try {
            task.run();
        } finally {
            sw.stop();
        }
        return sw.getTotal(TimeUnit.MILLISECONDS);
    }
}