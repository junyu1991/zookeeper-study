package com.yujun.zookeeper.base.lock;

import com.yujun.zookeeper.exception.ZookeeperLockException;
import org.apache.zookeeper.KeeperException;

/**
 * @author admin
 * @version 1.0.0
 * @date 2019/8/27 15:32
 * @description TODO
 **/
public interface ZookeeperLock {
    public void lock(String lockString) throws InterruptedException, KeeperException, ZookeeperLockException;
    public void lock(String lockString, int waitTime) throws InterruptedException, KeeperException, ZookeeperLockException;
    public void realease() throws InterruptedException, KeeperException, ZookeeperLockException;
    public String getLockString();
}
