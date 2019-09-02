package com.yujun.zookeeper.base.lock;

import com.yujun.zookeeper.base.Const;
import com.yujun.zookeeper.base.ZookeeperConnectConfig;
import com.yujun.zookeeper.exception.ZookeeperLockException;
import com.yujun.zookeeper.exception.ZookeeperLockUnreleaseException;
import com.yujun.zookeeper.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author admin
 * @version 1.0.0
 * @date 2019/8/27 15:40
 * @description TODO
 **/
@Slf4j
public class ZookeeperReadLock extends ZookeeperBaseLock {

    /** 用于和LockWatcher通讯 **/
    private BlockingQueue<String> blockingQueue = new LinkedBlockingQueue<String>();
    /** 等待锁时间 **/
    private int waitTime = 10000;

    public ZookeeperReadLock(ZookeeperConnectConfig config, String lockString) {
        super(config, lockString);
    }


    /**
     * 请求分布式锁
     *
     * @author: yujun
     * @date: 2019/8/26
     * @description: TODO
     * @return:
     * @exception:
     */
    @Override
    public void lock() throws InterruptedException, KeeperException, ZookeeperLockException, ZookeeperLockUnreleaseException {
        this.checkedLockRelease();
        String tempLockString = Const.READWRITELOCK + Const.ZOOKEEPERSEPRITE + lockString + Const.ZOOKEEPERSEPRITE  + Const.READ + lockString;
        String writeLockString = Const.WRITE + lockString;
        String nodeName = createNode(tempLockString, null);
        String path = Const.READWRITELOCK + Const.ZOOKEEPERSEPRITE + lockString;
        LockContainer.addLock(Thread.currentThread(), nodeName);
        String writeNode = getNextLowerNode(path, nodeName, writeLockString);
        log.info("Get Write node : " + writeNode);
        while(writeNode != null) {
            if(exists(writeNode, blockingQueue) != null) {
                blockingQueue.take();
                writeNode = getNextLowerNode(path, nodeName, writeLockString);
            } else {
                writeNode = getNextLowerNode(path, nodeName, writeLockString);
            }
        }
    }

    @Override
    public boolean lock(int waitTime, TimeUnit unit) throws InterruptedException, KeeperException, ZookeeperLockException, ZookeeperLockUnreleaseException {
        this.checkedLockRelease();
        String tempLockString = Const.READWRITELOCK + Const.ZOOKEEPERSEPRITE + lockString + Const.ZOOKEEPERSEPRITE  + Const.READ + lockString;
        String writeLockString = Const.WRITE + lockString;
        String nodeName = createNode(tempLockString, null);
        String path = Const.READWRITELOCK + Const.ZOOKEEPERSEPRITE + lockString;
        LockContainer.addLock(Thread.currentThread(), nodeName);
        String writeNode = getNextLowerNode(path, nodeName, writeLockString);
        log.info("Get Write node : " + writeNode);
        long lockTime = TimeUtil.toMicros(waitTime, unit);
        long start = System.currentTimeMillis();
        start = TimeUtil.toMicros(start, TimeUnit.MILLISECONDS);
        long now = TimeUtil.toMicros(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        while(writeNode != null) {
            now = TimeUtil.toMicros(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
            lockTime = lockTime - (now - start);
            if(exists(writeNode, blockingQueue) != null) {
                try {
                    String tempPath = blockingQueue.poll(lockTime, TimeUnit.MICROSECONDS);
                    if(tempPath != null) {
                        writeNode = getNextLowerNode(path, nodeName, writeLockString);
                    } else {
                        //获取锁超时，返回false，删除创建的path以防死锁
                        this.realease();
                        return false;
                    }
                } catch (InterruptedException e) {
                    this.realease();
                    return false;
                }
            } else {
                writeNode = getNextLowerNode(path, nodeName, writeLockString);
            }
        }
        return true;
    }

}
