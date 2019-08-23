package com.yujun.zookeeper;

import com.yujun.zookeeper.base.Connector;
import com.yujun.zookeeper.base.ZookeeperConnectConfig;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;

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
        connectConfig.setSessionTimeout(5000);
        Connector connector = Connector.getInstance(connectConfig);
        //connector.testAcl("/testacl");
        connector.createNodeIfNotExists("/lo");
        connector.createNodeIfNotExists("/locksd/asdfaf/cad", CreateMode.PERSISTENT_SEQUENTIAL, ZooDefs.Ids.OPEN_ACL_UNSAFE, "test".getBytes());
    }
}
