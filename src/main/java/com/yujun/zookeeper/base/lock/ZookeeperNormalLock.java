package com.yujun.zookeeper.base.lock;

import com.yujun.zookeeper.base.Const;
import com.yujun.zookeeper.base.ZookeeperConnectConfig;
import com.yujun.zookeeper.exception.ZookeeperLockException;
import com.yujun.zookeeper.exception.ZookeeperLockUnreleaseException;
import com.yujun.zookeeper.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author yujun
 * @version 1.0.0
 * @date 2019/8/29 10:24
 * @description TODO
 **/
@Slf4j
public class ZookeeperNormalLock extends ZookeeperBaseLock {

    public ZookeeperNormalLock(ZookeeperConnectConfig config, String lockString) {
        super(config, lockString);
    }

    @Override
    public void lock() throws InterruptedException, KeeperException, ZookeeperLockException, ZookeeperLockUnreleaseException {
        this.checkedLockRelease();
        String tempLockString = Const.LOCK + Const.ZOOKEEPERSEPRITE  + lockString;
        createPersistentNode(Const.LOCK, null);
        String fullNode = null;
        BlockingQueue<String> waitObject = new LinkedBlockingQueue<String>();
        while (fullNode == null) {
            try {
                fullNode = createNode(tempLockString, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                LockContainer.addLock(Thread.currentThread(), fullNode);
                break;
            } catch (KeeperException.NodeExistsException e) {
                fullNode = null;
                exists(tempLockString, waitObject);
                waitObject.take();
            }
        }
    }

    @Override
    public boolean lock(int waitTime, TimeUnit unit) throws InterruptedException, KeeperException, ZookeeperLockException, ZookeeperLockUnreleaseException {
        this.checkedLockRelease();
        String tempLockString = Const.LOCK + Const.ZOOKEEPERSEPRITE  + lockString;
        createPersistentNode(Const.LOCK, null);
        long lockTime = TimeUtil.toMicros(waitTime, unit);
        long start = System.currentTimeMillis();
        start = TimeUtil.toMicros(start, TimeUnit.MILLISECONDS);
        long now = TimeUtil.toMicros(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        String fullNode = null;
        BlockingQueue<String> waitObject = new LinkedBlockingQueue<String>();
        while(fullNode == null) {
            now = TimeUtil.toMicros(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
            lockTime = lockTime - (now - start);
            try {
                fullNode = createNode(tempLockString, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                LockContainer.addLock(Thread.currentThread(), fullNode);
                break;
            } catch (KeeperException.NodeExistsException e) {
                fullNode = null;
                exists(tempLockString, waitObject);
                try {
                    String poll = waitObject.poll(lockTime, TimeUnit.MICROSECONDS);
                    if (poll == null) {
                        return false;
                    }
                } catch (InterruptedException ex) {
                    return false;
                }
                continue;
            }

        }
        return true;
    }

}
