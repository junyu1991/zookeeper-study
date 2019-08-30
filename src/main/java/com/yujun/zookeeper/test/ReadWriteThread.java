package com.yujun.zookeeper.test;

import com.yujun.zookeeper.base.ZookeeperConnectConfig;
import com.yujun.zookeeper.base.ZookeeperConnector;
import com.yujun.zookeeper.base.lock.ZookeeperLock;
import com.yujun.zookeeper.base.lock.ZookeeperReadLock;
import com.yujun.zookeeper.base.lock.ZookeeperWriteLock;
import com.yujun.zookeeper.util.TimeUtil;

import java.util.concurrent.TimeUnit;

/**
 * @author admin
 * @version 1.0.0
 * @date 2019/8/27 10:38
 * @description TODO
 **/
public class ReadWriteThread extends Thread {

    private int sleepTime;
    private ZookeeperConnectConfig connector;
    private String lockName;
    public ReadWriteThread(int sleepTime, String name, ZookeeperConnectConfig connector, String lockName){
        super(name);
        this.sleepTime = sleepTime;
        this.connector = connector;
        this.lockName = lockName;
    }

    public void run() {
        //ZookeeperLockEnum readLock = ZookeeperLockEnum.READLOCK;
        ZookeeperLock lock;
        lock = new ZookeeperWriteLock(connector);
        lock = new ZookeeperReadLock(connector);
        while (true) {
            try {
                System.out.println(getName() + " try to get the read lock...");
                boolean lock1 = lock.lock(this.lockName, 15, TimeUnit.SECONDS);
                if(lock1) {
                    System.out.println(getName() + " get the read lock [" + lock.getLockString() + "] start to working....");
                    TimeUnit.SECONDS.sleep(this.sleepTime);
                    System.out.println(getName() + " work finished.");
                    lock.realease();
                } else {
                    System.out.println("Get lock failed");
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
