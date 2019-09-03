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
 * @date 2019/8/29 9:46
 * @description TODO
 **/
@Slf4j
public class ZookeeperSequentialLock extends ZookeeperBaseLock {

    public ZookeeperSequentialLock(ZookeeperConnectConfig config, String lockString) {
        super(config, lockString);
    }

    @Override
    public void lock() throws InterruptedException, KeeperException, ZookeeperLockException, ZookeeperLockUnreleaseException {
        this.checkedLockRelease();
        String tempLockString = Const.SEQUENTIALLOCK + Const.ZOOKEEPERSEPRITE  + lockString + Const.ZOOKEEPERSEPRITE + lockString;
        String fullNode = this.createNode(tempLockString, null);
        //log.info(Thread.currentThread().getName() + " create node [" + fullNode + "]");
        LockContainer.addLock(Thread.currentThread(), fullNode);
        String path = Const.SEQUENTIALLOCK + Const.ZOOKEEPERSEPRITE  + lockString;
        String lowerNode = getNextLowerNode(path, fullNode);
        BlockingQueue<String> waitObject = new LinkedBlockingQueue<String>();
        while(lowerNode != null) {
            if(exists(lowerNode, waitObject) != null) {
                log.info(Thread.currentThread().getName() + " set watch @ [" + lowerNode + "]");
                waitObject.take();
                log.info(Thread.currentThread().getName() + " has got the signal");
                lowerNode = getNextLowerNode(path, fullNode);
                log.info(Thread.currentThread().getName() + " get the new lowerNode " + lowerNode);
            } else {
                lowerNode = getNextLowerNode(path, fullNode);
            }
        }
    }

    @Override
    public boolean lock(int waitTime, TimeUnit unit) throws InterruptedException, KeeperException, ZookeeperLockException, ZookeeperLockUnreleaseException {
        this.checkedLockRelease();
        String tempLockString = Const.SEQUENTIALLOCK + Const.ZOOKEEPERSEPRITE  + lockString + Const.ZOOKEEPERSEPRITE + lockString;
        String fullNode = this.createNode(tempLockString, null);
        LockContainer.addLock(Thread.currentThread(), fullNode);
        String path = Const.SEQUENTIALLOCK + Const.ZOOKEEPERSEPRITE  + lockString;
        String lowerNode = this.getNextLowerNode(path, fullNode);
        long lockTime = TimeUtil.toMicros(waitTime, unit);
        long start = System.currentTimeMillis();
        start = TimeUtil.toMicros(start, TimeUnit.MILLISECONDS);
        long now = TimeUtil.toMicros(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        BlockingQueue<String> waitObject = new LinkedBlockingQueue<String>();
        while(lowerNode != null) {
            now = TimeUtil.toMicros(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
            lockTime = lockTime - (now - start);
            if(exists(lowerNode, waitObject) != null) {
                try {
                    String poll = waitObject.poll(lockTime, TimeUnit.MICROSECONDS);
                    if (poll == null) {
                        //获取锁超时，删除创建的path以防死锁
                        this.realease();
                        return false;
                    }
                    lowerNode = getNextLowerNode(path, fullNode);
                } catch (InterruptedException e) {
                    this.realease();
                    return false;
                }
            } else {
                lowerNode = getNextLowerNode(path, fullNode);
            }
        }
        return true;
    }

}
