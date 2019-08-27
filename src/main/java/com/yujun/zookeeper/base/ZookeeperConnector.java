package com.yujun.zookeeper.base;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import java.io.IOException;
import java.util.List;

/**
 * @author admin
 * @version 1.0.0
 * @date 2019/8/21 16:52
 * @description TODO
 **/
@Slf4j
public class ZookeeperConnector {
    /**  **/
    private static volatile ZookeeperConnector instance = null;
    /**  **/
    private ZooKeeper zooKeeper = null;

    private ZookeeperConnector(ZookeeperConnectConfig config){
        this.init(config);
    }

    public static ZookeeperConnector getInstance(ZookeeperConnectConfig config){
        synchronized (ZookeeperConnector.class) {
            if(instance == null)
                instance = new ZookeeperConnector(config);
            return instance;
        }
    }

    /**
     * @author: admin
     * @date: 2019/8/22
     * @description: TODO 
     * @param path
     * @return: 
     * @exception: 
    */
    public List<String> getChildren(String path) throws KeeperException, InterruptedException {
        List<String> children = zooKeeper.getChildren(path, false);
        return children;
    }

    /**
     * @author: admin
     * @date: 2019/8/22
     * @description: TODO 
     * @param config
     * @return: 
     * @exception: 
    */
    public void init(ZookeeperConnectConfig config){
        try {
            this.zooKeeper = new ZooKeeper(config.getConnectString(), config.getSessionTimeout(), new ConnectWatcher());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void close(){
        if(this.zooKeeper != null) {
            try {
                this.zooKeeper.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setAuth() {
        if(this.zooKeeper != null) {
            zooKeeper.addAuthInfo("digest", "username:password".getBytes());
        }
    }
    /**
     * 创建永久节点，可多级目录创建
     * @author: yujun
     * @date: 2019/8/23
     * @description: 创建永久节点，可实现多级目录创建
     * @param path
     * @return: {@link java.lang.String}
     * @exception: 
    */
    public synchronized String createNodeIfNotExists(String path) throws Exception {
        if(zooKeeper.exists(path, false) != null) {
            return path;
        }
        String[] split = path.split("/");
        StringBuilder sb = new StringBuilder();
        String result = "";
        sb.append("/");
        for(String s : split) {
            if("".equals(s) || s == null)
                continue;
            sb.append(s);
            if(zooKeeper.exists(sb.toString(), false) == null)
                result = zooKeeper.create(sb.toString(), null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            sb.append("/");
        }
        return result;
    }


    /**
     * 按照指定规则创建目录，可创建多级目录，
     * 若是多级目录，规则只对最终目录生效，对其他目录如其父级，祖父级目录均为默认规则：content为空，acl为<code>ZooDefs.Ids.OPEN_ACL_UNSAFE</code>,节点为永久节点
     * @author: admin
     * @date: 2019/8/23
     * @description: TODO
     * @param path
     * @param createMode
     * @param acl
     * @param data
     * @return: {@link String}
     * @exception:
    */
    public synchronized String createNodeIfNotExists(String path, CreateMode createMode, List<ACL> acl, byte[] data) throws KeeperException, InterruptedException {
        if(zooKeeper.exists(path, false) != null) {
            return path;
        }
        String[] split = path.split("/");
        StringBuilder sb = new StringBuilder();
        String result = "";
        sb.append("/");
        for(int i=0;i<split.length-1;i++) {
            if("".equals(split[i]) || split[i] == null)
                continue;
            sb.append(split[i]);
            if(zooKeeper.exists(sb.toString(), false) == null)
                result = zooKeeper.create(sb.toString(), null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            sb.append("/");
        }
        sb.append(split[split.length-1]);
        if(zooKeeper.exists(sb.toString(), false) == null)
            result = zooKeeper.create(sb.toString(), data, acl, createMode);
        return result;
    }

    /**
     * @author: admin
     * @date: 2019/8/23
     * @description: TODO
     * @param path
     * @param acl
     * @return:
     * @exception:
    */
    public void setAcl(String path, List<ACL> acl) throws KeeperException, InterruptedException {
        zooKeeper.setACL(path, acl, -1);
    }

    /**
     * 获取当前ZookeeperConnector中的zookeeper实例
     * @author: yujun
     * @date: 2019/8/26
     * @description: TODO
     * @param
     * @return: {@link org.apache.zookeeper.ZooKeeper}
     * @exception:
    */
    public ZooKeeper getZooKeeper() {
        return this.zooKeeper;
    }
}

/**
 * @author: admin
 * @date: 2019/8/22
 * @description: TODO
*/
class ConnectWatcher implements Watcher {
    public void process(WatchedEvent watchedEvent) {
    }
}
