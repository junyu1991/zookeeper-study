package com.yujun.zookeeper.test;

import com.yujun.zookeeper.base.ZookeeperConnector;
import com.yujun.zookeeper.base.lock.ZookeeperLock;
import com.yujun.zookeeper.base.lock.ZookeeperLockEnum;
import com.yujun.zookeeper.base.lock.ZookeeperReadLock;
import com.yujun.zookeeper.exception.ZookeeperLockException;
import org.apache.zookeeper.KeeperException;

import java.util.concurrent.TimeUnit;

/**
 * @author admin
 * @version 1.0.0
 * @date 2019/8/27 10:38
 * @description TODO
 **/
public class ReadThread extends Thread {

    private int sleepTime;
    private ZookeeperConnector connector;
    private String lockName;
    public ReadThread(int sleepTime, String name, ZookeeperConnector connector, String lockName){
        super(name);
        this.sleepTime = sleepTime;
        this.connector = connector;
        this.lockName = lockName;
    }

    public void run() {
        //ZookeeperLockEnum readLock = ZookeeperLockEnum.READLOCK;
        ZookeeperLock readLock = new ZookeeperReadLock(connector);
        while (true) {
            try {
                System.out.println(getName() + " try to get the read lock...");
                readLock.lock(this.lockName);
                System.out.println(getName() + " get the read lock [" + readLock.getLockString() +"] start to working....");
                TimeUnit.SECONDS.sleep(this.sleepTime);
                System.out.println(getName() + " work finished.");
                break;
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }
}