package com.yujun.zookeeper.test;

import lombok.extern.slf4j.Slf4j;

/**
 * @author hunter
 * @version 1.0.0
 * @date 8/27/19 8:36 PM
 * @description TODO
 **/
@Slf4j
public class TestThread extends Thread {

    private Object lockObject = new Object();

    public TestThread(String name, Object lockObject) {
        super(name);
        this.lockObject = lockObject;
    }

    public TestThread(String name) {
        super(name);
    }

    @Override
    public void run() {
        try {
            log.info(getName() + " waitting ");
            synchronized (lockObject) {
                log.info(getName() + " get lock  ");
                lockObject.wait();
                log.info(getName() + " finished");
            }
            //log.info("exit");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
