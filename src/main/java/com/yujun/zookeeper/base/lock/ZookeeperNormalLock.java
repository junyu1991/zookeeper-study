package com.yujun.zookeeper.base.lock;

import com.yujun.zookeeper.base.Const;
import com.yujun.zookeeper.base.ZookeeperConnectConfig;
import com.yujun.zookeeper.exception.ZookeeperLockException;
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
public class ZookeeperNormalLock extends ZookeeperBaseLock {

    private String lockString;
    private BlockingQueue<String> waitObject = new LinkedBlockingQueue<String>();

    public ZookeeperNormalLock(ZookeeperConnectConfig config) {
        super(config);
    }

    @Override
    public void lock(String lockString) throws InterruptedException, KeeperException, ZookeeperLockException {
        this.lockString = Const.LOCK + Const.ZOOKEEPERSEPRITE  + lockString;
        while(exists(this.lockString, waitObject) != null) {
            waitObject.take();
            try {
                this.lockString = createEphemeralNode(this.lockString, null);
                break;
            } catch (KeeperException.NodeExistsException e) {
                continue;
            }
        }
    }

    @Override
    public boolean lock(String lockString, int waitTime, TimeUnit unit) throws InterruptedException, KeeperException, ZookeeperLockException {
        this.lockString = Const.LOCK + Const.ZOOKEEPERSEPRITE  + lockString;
        long lockTime = TimeUtil.toMicros(waitTime, unit);
        long start = System.currentTimeMillis();
        start = TimeUtil.toMicros(start, TimeUnit.MILLISECONDS);
        long now = TimeUtil.toMicros(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        while(exists(this.lockString, waitObject) != null) {
            now = TimeUtil.toMicros(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
            lockTime = lockTime - (now - start);
            try {
                String poll = waitObject.poll(lockTime, unit);
                if(poll == null)
                    return false;
            } catch (InterruptedException e) {
                return false;
            }
            try {
                this.lockString = createEphemeralNode(this.lockString, null);
                break;
            } catch (KeeperException.NodeExistsException e) {
                continue;
            }
        }
        return true;
    }


    @Override
    public void realease() throws InterruptedException, KeeperException, ZookeeperLockException {
        if(this.lockString != null)
            this.deletePath(this.lockString);
    }

    @Override
    public String getLockString() {
        return this.lockString;
    }
}
