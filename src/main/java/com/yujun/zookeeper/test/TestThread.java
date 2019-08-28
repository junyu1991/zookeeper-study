package com.yujun.zookeeper.test;

/**
 * @author hunter
 * @version 1.0.0
 * @date 8/27/19 8:36 PM
 * @description TODO
 **/
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
            System.out.println(getName() + " waitting ");
            synchronized (lockObject) {
                System.out.println(getName() + " get lock  ");
                lockObject.wait();
                System.out.println(getName() + " finished");
            }
            //System.out.println("exit");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
