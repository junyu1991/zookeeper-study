package com.yujun.zookeeper.test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author admin
 * @version 1.0.0
 * @date 2019/8/27 15:18
 * @description TODO
 **/
@Slf4j
public class EnumThread extends Thread{
    public void run() {
        TestEnum t = TestEnum.TEST;
        log.info(t.getMessage());
        t.setMessage("Hello");
        try {
            TimeUnit.SECONDS.sleep(2);
            log.info(t.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
