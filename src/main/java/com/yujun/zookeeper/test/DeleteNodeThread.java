package com.yujun.zookeeper.test;

import com.yujun.zookeeper.base.ZookeeperConnector;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;

import java.util.concurrent.TimeUnit;

/**
 * @author admin
 * @version 1.0.0
 * @date 2019/8/28 10:19
 * @description TODO
 **/
@Slf4j
public class DeleteNodeThread extends Thread{
    private ZookeeperConnector connector;
    private String path;

    public DeleteNodeThread(String name, String path, ZookeeperConnector connector){
        super(name);
        this.path = path;
        this.connector = connector;
    }

    @Override
    public void run() {
        try {
            log.info(getName() + " delete thread start");
            //TimeUnit.SECONDS.sleep(20);
            this.connector.getZooKeeper().delete(this.path, -1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }
}
