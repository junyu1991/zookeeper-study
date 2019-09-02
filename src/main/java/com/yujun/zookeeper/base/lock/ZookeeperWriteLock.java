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
 * @date 2019/8/28 15:43
 * @description TODO
 **/
@Slf4j
public class ZookeeperWriteLock extends ZookeeperBaseLock {

    private BlockingQueue<String> waitObject = new LinkedBlockingQueue<String>();

    public ZookeeperWriteLock(ZookeeperConnectConfig config, String lockString) {
        super(config, lockString);
    }

    @Override
    public void lock() throws InterruptedException, KeeperException, ZookeeperLockException, ZookeeperLockUnreleaseException {
        this.checkedLockRelease();
        String tempLockString = Const.READWRITELOCK + Const.ZOOKEEPERSEPRITE + lockString + Const.ZOOKEEPERSEPRITE  + Const.WRITE + lockString;
        String nodeName = createNode(tempLockString, null);
        String path = Const.READWRITELOCK + Const.ZOOKEEPERSEPRITE + lockString;
        LockContainer.addLock(Thread.currentThread(), nodeName);
        String writeNode = getNextLowerNode(path, nodeName);
        log.info("Get Write node : " + writeNode);
        while(writeNode != null) {
            if(exists(writeNode, waitObject) != null) {
                this.waitObject.take();
                writeNode = getNextLowerNode(path, nodeName);
            } else {
                writeNode = getNextLowerNode(path, nodeName);
            }
        }
    }

    @Override
    public boolean lock(int waitTime, TimeUnit unit) throws InterruptedException, KeeperException, ZookeeperLockException, ZookeeperLockUnreleaseException {
        this.checkedLockRelease();
        String tempLockString = Const.READWRITELOCK + Const.ZOOKEEPERSEPRITE + lockString + Const.ZOOKEEPERSEPRITE  + Const.WRITE + lockString;
        String nodeName = createNode(tempLockString, null);
        String path = Const.READWRITELOCK + Const.ZOOKEEPERSEPRITE + lockString;
        LockContainer.addLock(Thread.currentThread(), nodeName);
        String writeNode = getNextLowerNode(path, nodeName);
        long lockTime = TimeUtil.toMicros(waitTime, unit);
        long start = System.currentTimeMillis();
        start = TimeUtil.toMicros(start, TimeUnit.MILLISECONDS);
        long now = TimeUtil.toMicros(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        log.info("Get Write node : " + writeNode);
        while(writeNode != null) {
            now = TimeUtil.toMicros(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
            lockTime = lockTime - (now - start);
            if(exists(writeNode, waitObject) != null) {
                try {
                    String poll = this.waitObject.poll(lockTime, TimeUnit.MICROSECONDS);
                    if(poll == null) {
                        //获取锁超时，删除创建的path以防死锁
                        this.realease();
                        return false;
                    }
                    writeNode = getNextLowerNode(path, nodeName);
                } catch (InterruptedException e) {
                    this.realease();
                    return false;
                }
            } else {
                writeNode = getNextLowerNode(path, nodeName);
            }
        }
        return true;
    }

}
