package com.yujun.zookeeper;

import com.yujun.zookeeper.base.Connector;

/**
 * @author admin
 * @version 1.0.0
 * @date 2019/8/21 16:51
 * @description TODO
 **/
public class MainRunner {
    public static void main(String[] args) {
        Connector c = new Connector();
        c.connect();
    }
}
