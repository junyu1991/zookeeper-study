package com.yujun.zookeeper.base.lock;

import com.yujun.zookeeper.base.Const;
import com.yujun.zookeeper.base.ZookeeperConnectConfig;
import com.yujun.zookeeper.base.ZookeeperConnector;
import com.yujun.zookeeper.exception.ZookeeperLockException;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author admin
 * @version 1.0.0
 * @date 2019/8/29 9:46
 * @description TODO
 **/
public class ZookeeperSequentialLock extends ZookeeperBaseLock {

    private String lockPath;
    private BlockingQueue<String> waitObject;

    public ZookeeperSequentialLock(ZookeeperConnectConfig config) {
        super(config);
    }

    @Override
    public void lock(String lockString) throws InterruptedException, KeeperException, ZookeeperLockException {
        this.lockPath = Const.SEQUENTIALLOCK + Const.ZOOKEEPERSEPRITE  + lockString + Const.ZOOKEEPERSEPRITE + lockString;
        String fullNode = this.createNode(this.lockPath, null);
        this.lockPath = fullNode;
        String path = Const.SEQUENTIALLOCK + Const.ZOOKEEPERSEPRITE  + lockString;
        String lowerNode = this.getNextLowerNode(path, fullNode);
        while(lowerNode != null) {
            if(exists(lowerNode, waitObject) != null) {
               this.waitObject.take();
               lowerNode = getNextLowerNode(path, lowerNode);
            } else {
                lowerNode = getNextLowerNode(path, lowerNode);
            }
        }
    }

    @Override
    public boolean lock(String lockString, int waitTime, TimeUnit unit) throws InterruptedException, KeeperException, ZookeeperLockException {
        this.lockPath = Const.SEQUENTIALLOCK + Const.ZOOKEEPERSEPRITE  + lockString + Const.ZOOKEEPERSEPRITE + lockString;
        String fullNode = this.createNode(this.lockPath, null);
        this.lockPath = fullNode;
        String path = Const.SEQUENTIALLOCK + Const.ZOOKEEPERSEPRITE  + lockString;
        String lowerNode = this.getNextLowerNode(path, fullNode);
        while(lowerNode != null) {
            if(exists(lowerNode, waitObject) != null) {
                try {
                    String poll = this.waitObject.poll(waitTime, unit);
                    if (poll == null)
                        return false;
                    lowerNode = getNextLowerNode(path, lowerNode);
                } catch (InterruptedException e) {
                    return false;
                }
            } else {
                lowerNode = getNextLowerNode(path, lowerNode);
            }
        }
        return true;
    }

    @Override
    public void realease() throws InterruptedException, KeeperException, ZookeeperLockException {
        if(this.lockPath != null)
            this.deletePath(this.lockPath);
    }

    @Override
    public String getLockString() {
        return this.lockPath;
    }
}
