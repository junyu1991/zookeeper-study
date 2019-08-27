package com.yujun.zookeeper.test;

/**
 * @author admin
 * @version 1.0.0
 * @date 2019/8/27 15:18
 * @description TODO
 **/
public class TestRunner {
    public static void main(String[] args) {
        TestEnum t = TestEnum.TEST;
        t.setMessage("llk");
        EnumThread e = new EnumThread();
        e.start();
        System.out.println(t.getMessage());

    }
}
