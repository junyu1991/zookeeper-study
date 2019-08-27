package com.yujun.zookeeper;

import com.yujun.zookeeper.base.ZookeeperConnectConfig;
import com.yujun.zookeeper.base.ZookeeperConnector;
import com.yujun.zookeeper.test.ReadThread;
import com.yujun.zookeeper.util.ZookeeperCompartor;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;

import java.util.List;

/**
 * @author admin
 * @version 1.0.0
 * @date 2019/8/21 16:51
 * @description TODO
 **/
public class MainRunner {
    public static void main(String[] args) throws Exception {

        String connectString = "172.18.1.109:2181";
        ZookeeperConnectConfig connectConfig = new ZookeeperConnectConfig();
        connectConfig.setConnectString(connectString);
        connectConfig.setSessionTimeout(500000);
        ZookeeperConnector connector = ZookeeperConnector.getInstance(connectConfig);
        String lockName = "test-read";
        ReadThread rt1 = new ReadThread(1,"read-thread-1", connector, lockName);
        ReadThread rt2 = new ReadThread(2, "read-thread-2", connector, lockName);
        ReadThread rt3 = new ReadThread(3, "read-thread-3", connector, lockName);
        ReadThread rt4 = new ReadThread(5, "read-thread-4", connector, lockName);
        rt3.start();
        rt4.start();
        rt1.start();
        rt2.start();

        /*List<String> children = connector.getZooKeeper().getChildren("/test", false);
        children.sort(new ZookeeperCompartor());
        for(String c : children)
            System.out.println(c);*/

        /*
        String node = "test0000000000";
        System.out.println(node.substring(0, node.length()-10));
        System.out.println(Long.parseLong(node.substring(node.length()-10, node.length())));
        System.out.println(Long.parseLong(node.substring(node.length()-10, node.length())) == 0);
        */
    }
}
