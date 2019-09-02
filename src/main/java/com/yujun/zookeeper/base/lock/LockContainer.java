package com.yujun.zookeeper.base.lock;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yujun
 * @version 1.0.0
 * @date 2019/9/2 11:03
 * @description TODO
 **/
public class LockContainer {
    /** 用于全局存储各个锁 **/
    private static Map<Thread, String> lockMap = new ConcurrentHashMap<Thread, String>();

    /**
     * 存放锁
     * @author: yujun
     * @date: 2019/9/2
     * @description: TODO
     * @param t
     * @param lockString
     * @return:
     * @exception:
    */
    protected static void addLock(Thread t, String lockString) {
        lockMap.put(t, lockString);
    }

    /**
     * 获取锁
     * @author: yujun
     * @date: 2019/9/2
     * @description: TODO
     * @param t
     * @return: {@link String}
     * @exception:
    */
    protected static String getLockString(Thread t) {
        return lockMap.get(t);
    }

    /** 
     * 获取锁，并将锁从map中删除
     * @author: yujun
     * @date: 2019/9/2
     * @description: TODO 
     * @param t
     * @return: {@link String}
     * @exception: 
    */
    protected static String getLockStringRemoved(Thread t) {
        return lockMap.remove(t);
    }

    /** 
     * 判断当前LockContainer是否存在特定锁
     * @author: yujun
     * @date: 2019/9/2
     * @description: TODO 
     * @param t
     * @return: {@link boolean}
     * @exception: 
    */
    protected static boolean containLock(Thread t){
        return lockMap.containsKey(t);
    }
}
