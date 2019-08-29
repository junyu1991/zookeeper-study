package com.yujun.zookeeper.base.lock;

import com.yujun.zookeeper.exception.ZookeeperLockException;
import org.apache.zookeeper.KeeperException;

import java.util.concurrent.TimeUnit;

/**
 * @author yujun
 * @version 1.0.0
 * @date 2019/8/27 15:32
 * @description TODO
 **/
public interface ZookeeperLock {
    /** 
     * 尝试获取分布式锁 lockString
     * @author: yujun
     * @date: 2019/8/29
     * @description: TODO 
     * @param lockString
     * @return: 
     * @exception: 
    */
    public void lock(String lockString) throws InterruptedException, KeeperException, ZookeeperLockException;
    /** 
     * 尝试获取分布式锁 lockString，设置了超时，若超时获取锁失败返回false，成功则返回true
     * @author: yujun
     * @date: 2019/8/29
     * @description: TODO 
     * @param lockString
     * @param waitTime
     * @param unit
     * @return: 
     * @exception: 
    */
    public boolean lock(String lockString, int waitTime, TimeUnit unit) throws InterruptedException, KeeperException, ZookeeperLockException;
    /** 
     * 释放当前占用的分布式锁
     * @author: yujun
     * @date: 2019/8/29
     * @description: TODO 
     * @param 
     * @return: 
     * @exception: 
    */
    public void realease() throws InterruptedException, KeeperException, ZookeeperLockException;
    /** 
     * 获取当前的锁
     * @author: yujun
     * @date: 2019/8/29
     * @description: TODO 
     * @param 
     * @return: {@link String}
     * @exception: 
    */
    public String getLockString();
}
