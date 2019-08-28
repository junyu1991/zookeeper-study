package com.yujun.zookeeper.base.lock;

import com.yujun.zookeeper.base.Const;
import com.yujun.zookeeper.base.ZookeeperConnectConfig;
import com.yujun.zookeeper.base.ZookeeperConnector;
import com.yujun.zookeeper.exception.ZookeeperLockException;
import org.apache.zookeeper.KeeperException;

/**
 * @author admin
 * @version 1.0.0
 * @date 2019/8/28 15:43
 * @description TODO
 **/
public class ZookeeperWriteLock extends ZookeeperBaseLock {

    private String writeLock = null;
    private ZookeeperWaitObject waitObject;

    public ZookeeperWriteLock(ZookeeperConnector zookeeperConnector) {
        super(zookeeperConnector);
    }

    public ZookeeperWriteLock(ZookeeperConnectConfig config) {
        super(config);
    }

    @Override
    public void lock(String lockString) throws InterruptedException, KeeperException, ZookeeperLockException {
        this.writeLock = Const.READWRITELOCK + Const.ZOOKEEPERSEPRITE + lockString + Const.ZOOKEEPERSEPRITE  + Const.WRITE + lockString;
        String nodeName = createNode(writeLock, null);
        String path = Const.READWRITELOCK + Const.ZOOKEEPERSEPRITE + lockString;
        this.writeLock = nodeName;
        String writeNode = getNextLowerNode(path, nodeName);
        System.out.println("Get Write node : " + writeNode);
        waitObject = new ZookeeperWaitObject(false);
        while(writeNode != null) {
            waitObject.setNotified(false);
            if(exists(writeNode, waitObject) != null) {
                if(!waitObject.isNotified()) {
                    synchronized (waitObject) {
                        waitObject.wait();
                        writeNode = getNextLowerNode(path, nodeName);
                    }
                } else {
                    writeNode = getNextLowerNode(path, nodeName);
                }
            } else {
                writeNode = getNextLowerNode(path, nodeName);
            }
        }
    }

    @Override
    public void lock(String lockString, int waitTime) throws InterruptedException, KeeperException, ZookeeperLockException {

    }

    @Override
    public void realease() throws InterruptedException, KeeperException, ZookeeperLockException {
        if(this.writeLock != null) {
            this.deletePath(this.writeLock);
        }
    }

    @Override
    public String getLockString() {
        return this.writeLock;
    }
}
