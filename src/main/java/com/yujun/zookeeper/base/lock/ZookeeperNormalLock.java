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
 * @author yujun
 * @version 1.0.0
 * @date 2019/8/29 10:24
 * @description TODO
 **/
@Slf4j
@Deprecated
public class ZookeeperNormalLock extends ZookeeperBaseLock {

    private BlockingQueue<String> waitObject = new LinkedBlockingQueue<String>();

    public ZookeeperNormalLock(ZookeeperConnectConfig config, String lockString) {
        super(config, lockString);
    }

    @Override
    public void lock() throws InterruptedException, KeeperException, ZookeeperLockException, ZookeeperLockUnreleaseException {
        this.checkedLockRelease();
        String tempLockString = Const.LOCK + Const.ZOOKEEPERSEPRITE  + lockString;
        String fullNode = null;
        if(exists(tempLockString, waitObject) != null) {
            while (fullNode == null) {
                System.out.println("Set watch...." + tempLockString);
                waitObject.take();
                try {
                    fullNode = createEphemeralNode(tempLockString, null);
                    LockContainer.addLock(Thread.currentThread(), fullNode);
                    break;
                } catch (KeeperException.NodeExistsException e) {
                    fullNode = null;
                    continue;
                }
            }
        } else {

        }
    }

    @Override
    public boolean lock(int waitTime, TimeUnit unit) throws InterruptedException, KeeperException, ZookeeperLockException, ZookeeperLockUnreleaseException {
        this.checkedLockRelease();
        String tempLockString = Const.LOCK + Const.ZOOKEEPERSEPRITE  + lockString;
        long lockTime = TimeUtil.toMicros(waitTime, unit);
        long start = System.currentTimeMillis();
        start = TimeUtil.toMicros(start, TimeUnit.MILLISECONDS);
        long now = TimeUtil.toMicros(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        String fullNode = null;
        try {
            fullNode = createEphemeralNode(tempLockString, null);
            LockContainer.addLock(Thread.currentThread(), fullNode);
            return true;
        } catch(KeeperException.NodeExistsException e) {
            fullNode = null;
        }
        while(fullNode == null) {
            exists(tempLockString, waitObject);
            now = TimeUtil.toMicros(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
            lockTime = lockTime - (now - start);
            try {
                String poll = waitObject.poll(lockTime, TimeUnit.MICROSECONDS);
                if(poll == null) {
                    return false;
                } else {
                    if(exists(fullNode, waitObject) == null) {
                        try {
                            fullNode = createEphemeralNode(tempLockString, null);
                            LockContainer.addLock(Thread.currentThread(), fullNode);
                            break;
                        } catch (KeeperException.NodeExistsException e) {
                            fullNode = null;
                            continue;
                        }
                    }
                }
            } catch (InterruptedException e) {
                return false;
            }

        }
        return true;
    }

}
