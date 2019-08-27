package com.yujun.zookeeper.test;

import java.util.concurrent.TimeUnit;

/**
 * @author admin
 * @version 1.0.0
 * @date 2019/8/27 15:18
 * @description TODO
 **/
public class EnumThread extends Thread{
    public void run() {
        TestEnum t = TestEnum.TEST;
        System.out.println(t.getMessage());
        t.setMessage("Hello");
        try {
            TimeUnit.SECONDS.sleep(2);
            System.out.println(t.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
