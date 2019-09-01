package com.yujun.zookeeper.test;

import com.yujun.zookeeper.base.ZookeeperConnectConfig;
import com.yujun.zookeeper.base.lock.ZookeeperLock;
import com.yujun.zookeeper.base.lock.ZookeeperSequentialLock;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yujun
 * @version 1.0.0
 * @date 2019/8/29 11:31
 * @description TODO
 **/
@Slf4j
public class SequentialThread extends Thread{
    private ZookeeperConnectConfig connectConfig;
    private String lockName;
    public SequentialThread(String name, ZookeeperConnectConfig connectConfig, String lockName){
        super(name);
        this.connectConfig = connectConfig;
        this.lockName = lockName;
    }

    @Override
    public void run() {
        ZookeeperLock sequentialLock = new ZookeeperSequentialLock(connectConfig);
        while (true){

        }
    }
}
