package com.yujun.zookeeper.util;

import java.util.concurrent.TimeUnit;

/**
 * @author yujun
 * @version 1.0.0
 * @date 2019/8/30 13:47
 * @description TODO
 **/
public class TimeUtil {

    /** 
     * 将指定时间转换成微秒
     * @author: yujun
     * @date: 2019/8/30
     * @description: TODO 
     * @param time
     * @param timeUnit
     * @return: {@link long}
     * @exception: 
    */
    public static long toMicros(long time, TimeUnit timeUnit) {
        return timeUnit.toMicros(time);
    }
}
