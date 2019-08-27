package com.yujun.zookeeper.test;

/**
 * @author hunter
 * @version 1.0.0
 * @date 8/27/19 10:07 PM
 * @description TODO
 **/
public class NotifyThread extends Thread {
    private Object notifyObject;

    public NotifyThread(Object notifyObject) {
        this.notifyObject = notifyObject;
    }
    public void run(){
        synchronized (notifyObject) {
            notifyObject.notifyAll();
        }
    }
}
