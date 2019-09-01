package com.yujun.zookeeper;

import com.yujun.zookeeper.base.Const;
import com.yujun.zookeeper.base.ZookeeperConnectConfig;
import com.yujun.zookeeper.test.DeleteNodeThread;
import com.yujun.zookeeper.test.ReadWriteThread;
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
        
        String lockName = "test-read";
        //String writeNode = connectConfig.getZooKeeper().create(Const.READWRITELOCK+Const.ZOOKEEPERSEPRITE+Const.WRITE + lockName,null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        //log.info("Create write node : " + writeNode);


        //TimeUnit.MILLISECONDS.sleep(500);
        //dn.start();
        /*List<String> children = connectConfig.getZooKeeper().getChildren("/test", false);
        children.sort(new ZookeeperCompartor());
        for(String c : children)
            log.info(c);*/

        /*
        String node = "test0000000000";
        log.info(node.substring(0, node.length()-10));
        log.info(Long.parseLong(node.substring(node.length()-10, node.length())));
        log.info(Long.parseLong(node.substring(node.length()-10, node.length())) == 0);
        */
    }

    public static void testRead(ZookeeperConnectConfig config, String lockName) {
        ReadWriteThread rt1 = new ReadWriteThread(1,"read-thread-1", config, lockName);
        ReadWriteThread rt2 = new ReadWriteThread(2, "read-thread-2", config, lockName);
        ReadWriteThread rt3 = new ReadWriteThread(3, "read-thread-3", config, lockName);
        ReadWriteThread rt4 = new ReadWriteThread(5, "read-thread-4", config, lockName);
        //DeleteNodeThread dn = new DeleteNodeThread("Delete-thread", writeNode, connectConfig);
        rt3.start();
        rt4.start();
        rt1.start();
        rt2.start();
    }

    public static void testSequentialLock(ZookeeperConnectConfig config, String lockName) {

    }
}
