package com.yujun.zookeeper.base.lock;

import com.yujun.zookeeper.base.Const;
import com.yujun.zookeeper.base.ZookeeperConnectConfig;
import com.yujun.zookeeper.exception.ZookeeperLockException;
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
public class ZookeeperReadLock extends ZookeeperBaseLock {

    /** 用于和LockWatcher通讯 **/
    private BlockingQueue<String> blockingQueue = new LinkedBlockingQueue<String>();
    /**  **/
    private String readLockString = null;
    /** 等待锁时间 **/
    private int waitTime = 10000;

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
        this.readLockString = Const.READWRITELOCK + Const.ZOOKEEPERSEPRITE + lockString + Const.ZOOKEEPERSEPRITE  + Const.READ + lockString;
        String writeLockString = Const.WRITE + lockString;
        String nodeName = createNode(readLockString, null);
        String path = Const.READWRITELOCK + Const.ZOOKEEPERSEPRITE + lockString;
        this.readLockString = nodeName;
        String writeNode = getNextLowerNode(path, nodeName,writeLockString);
        System.out.println("Get Write node : " + writeNode);
        while(writeNode != null) {
            if(exists(writeNode, blockingQueue) != null) {
                blockingQueue.take();
                writeNode = getNextLowerNode(path, nodeName, writeLockString);
            } else {
                writeNode = getNextLowerNode(path, nodeName,writeLockString);
            }
        }
    }

    @Override
    public boolean lock(String lockString, int waitTime, TimeUnit unit) throws InterruptedException, KeeperException, ZookeeperLockException {
        this.readLockString = Const.READWRITELOCK + Const.ZOOKEEPERSEPRITE + lockString + Const.ZOOKEEPERSEPRITE  + Const.READ + lockString;
        String writeLockString = Const.WRITE + lockString;
        String nodeName = createNode(readLockString, null);
        String path = Const.READWRITELOCK + Const.ZOOKEEPERSEPRITE + lockString;
        this.readLockString = nodeName;
        String writeNode = getNextLowerNode(path, nodeName,writeLockString);
        System.out.println("Get Write node : " + writeNode);
        while(writeNode != null) {
            if(exists(writeNode, blockingQueue) != null) {
                try {
                    String tempPath = blockingQueue.poll(waitTime, unit);
                    if(tempPath != null)
                        writeNode = getNextLowerNode(path, nodeName, writeLockString);
                    else {
                        //获取锁超时，返回false
                        return false;
                    }
                } catch (InterruptedException e) {
                    return false;
                }
            } else {
                writeNode = getNextLowerNode(path, nodeName,writeLockString);
            }
        }
        return true;
    }

    @Override
    public void realease() throws InterruptedException, KeeperException, ZookeeperLockException {
        if(this.readLockString != null)
            this.deletePath(this.readLockString);
    }

    @Override
    public String getLockString() {
        return this.readLockString;
    }
}
