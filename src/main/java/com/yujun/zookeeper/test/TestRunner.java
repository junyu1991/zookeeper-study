package com.yujun.zookeeper.test;

import com.yujun.zookeeper.base.ZookeeperConnectConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author admin
 * @version 1.0.0
 * @date 2019/8/27 15:18
 * @description TODO
 **/
@Slf4j
public class TestRunner {
    public static void main(String[] args) throws InterruptedException {
        /*Object object1 = new Object();
        Object object2 = new Object();
        TestThread t1 = new TestThread("TestThread-1", object1);
        TestThread t2 = new TestThread("TestThread-2", object2);
        NotifyThread n1 = new NotifyThread("notify-thread-1",object1);
        NotifyThread n2 = new NotifyThread("notify-thread-2",object2);
        t1.start();
        t2.start();
        n1.start();
        n2.start();*/
        //log.info("finished");
        //n2.start();
        //TimeUnit.SECONDS.sleep(1);
        /*TimeUnit.SECONDS.sleep(1);
        synchronized (object1){
            object1.notify();
        }
        TimeUnit.SECONDS.sleep(1);
        synchronized (object2) {
            object2.notify();
        }*/

        ZookeeperConnectConfig config = new ZookeeperConnectConfig("127.0.0.1:2181", 5000);
        String lockName = "test-time-lock";
        getReadLock(config, lockName);
    }

    public static void getReadLock(ZookeeperConnectConfig config, String lockName) {
        ReadWriteThread readWriteThread = new ReadWriteThread(1, "Time-Thread", config, lockName);
        readWriteThread.start();
        log.info("Start....");
    }
}
