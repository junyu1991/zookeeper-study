package com.yujun.zookeeper.test;

/**
 * @author hunter
 * @version 1.0.0
 * @date 8/27/19 10:07 PM
 * @description TODO
 **/
public class NotifyThread extends Thread {
    private Object notifyObject;

    public NotifyThread(String name, Object notifyObject) {
        super(name);
        this.notifyObject = notifyObject;
    }
    public void run(){
        System.out.println(getName() + " try to get Object lock");
        synchronized (notifyObject) {
            notifyObject.notifyAll();
        }
        System.out.println(getName() + " notify ");
    }
}
