package com.yujun.zookeeper.base.lock;

import com.yujun.zookeeper.base.Const;
import com.yujun.zookeeper.base.ZookeeperConnectConfig;
import com.yujun.zookeeper.base.ZookeeperConnector;
import com.yujun.zookeeper.exception.ZookeeperLockException;
import org.apache.zookeeper.KeeperException;

/**
 * @author admin
 * @version 1.0.0
 * @date 2019/8/27 15:40
 * @description TODO
 **/
public class ZookeeperReadLock extends ZookeeperBaseLock {

    /** 用于线程同步的Object **/
    private Object readObject;
    /**  **/
    private String readLockString = null;

    public ZookeeperReadLock(ZookeeperConnector zookeeperConnector) {
        super(zookeeperConnector);
    }
    public ZookeeperReadLock(ZookeeperConnectConfig config) {
        super(config);
    }


    /**
     * 请求分布式锁
     *
     * @param lockString
     * @author: yujun
     * @date: 2019/8/26
     * @description: TODO
     * @return:
     * @exception:
     */
    @Override
    public void lock(String lockString) throws InterruptedException, KeeperException, ZookeeperLockException {
        this.readLockString = Const.READWRITELOCK + Const.ZOOKEEPERSEPRITE  + Const.READ + lockString;
        String writeLockString = Const.WRITE + lockString;
        String nodeName = createNode(readLockString, null);
        this.readLockString = nodeName;
        String writeNode = getNextLowerNode(Const.READWRITELOCK, nodeName,writeLockString);
        readObject = new Object();
        while(writeNode != null) {
            if(exists(writeNode, readObject) != null) {
                readObject.wait();
                writeNode = getNextLowerNode(Const.READWRITELOCK, nodeName,writeLockString);
            } else {
                writeNode = getNextLowerNode(Const.READWRITELOCK, nodeName,writeLockString);
            }
        }
    }

    @Override
    public void realease() throws InterruptedException, KeeperException, ZookeeperLockException {
        this.deletePath(this.readLockString);
    }

    @Override
    public String getLockString() {
        return this.readLockString;
    }
}
