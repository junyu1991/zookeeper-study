package com.yujun.zookeeper.test;

import lombok.extern.slf4j.Slf4j;

/**
 * @author hunter
 * @version 1.0.0
 * @date 8/27/19 10:07 PM
 * @description TODO
 **/
@Slf4j
public class NotifyThread extends Thread {
    private Object notifyObject;

    public NotifyThread(String name, Object notifyObject) {
        super(name);
        this.notifyObject = notifyObject;
    }
    public void run(){
        log.info(getName() + " try to get Object lock");
        synchronized (notifyObject) {
            notifyObject.notifyAll();
        }
        log.info(getName() + " notify ");
    }
}
