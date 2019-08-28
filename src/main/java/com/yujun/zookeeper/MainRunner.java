package com.yujun.zookeeper;

import com.yujun.zookeeper.base.Const;
import com.yujun.zookeeper.base.ZookeeperConnectConfig;
import com.yujun.zookeeper.base.ZookeeperConnector;
import com.yujun.zookeeper.test.DeleteNodeThread;
import com.yujun.zookeeper.test.ReadThread;
import com.yujun.zookeeper.util.ZookeeperCompartor;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author admin
 * @version 1.0.0
 * @date 2019/8/21 16:51
 * @description TODO
 **/
public class MainRunner {
    public static void main(String[] args) throws Exception {

        String connectString = "127.0.0.1:2181";
        ZookeeperConnectConfig connectConfig = new ZookeeperConnectConfig();
        connectConfig.setConnectString(connectString);
        connectConfig.setSessionTimeout(500000);
        ZookeeperConnector connector = ZookeeperConnector.getInstance(connectConfig);
        String lockName = "test-read";
        //String writeNode = connector.getZooKeeper().create(Const.READWRITELOCK+Const.ZOOKEEPERSEPRITE+Const.WRITE + lockName,null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        //System.out.println("Create write node : " + writeNode);
        ReadThread rt1 = new ReadThread(1,"read-thread-1", connector, lockName);
        ReadThread rt2 = new ReadThread(2, "read-thread-2", connector, lockName);
        ReadThread rt3 = new ReadThread(3, "read-thread-3", connector, lockName);
        ReadThread rt4 = new ReadThread(5, "read-thread-4", connector, lockName);
        //DeleteNodeThread dn = new DeleteNodeThread("Delete-thread", writeNode, connector);
        rt3.start();
        rt4.start();
        rt1.start();
        rt2.start();

        //TimeUnit.MILLISECONDS.sleep(500);
        //dn.start();
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
