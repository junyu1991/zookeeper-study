package com.yujun.zookeeper.base;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author admin
 * @version 1.0.0
 * @date 2019/8/21 16:52
 * @description TODO
 **/
@Slf4j
public class Connector {
    /**  **/
    private static volatile Connector instance = null;
    /**  **/
    private ZooKeeper zooKeeper = null;

    private Connector(ZookeeperConnectConfig config){
        this.init(config);
    }

    public static Connector getInstance(ZookeeperConnectConfig config){
        synchronized (Connector.class) {
            if(instance == null)
                instance = new Connector(config);
            return instance;
        }
    }

    public void lock(String lockPath){
        if(this.zooKeeper == null){
            return;
        }
        try {
            String path = this.zooKeeper.create(lockPath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void release(String lockPath) {

    }

    /**
     * @author: admin
     * @date: 2019/8/22
     * @description: TODO 
     * @param path
     * @return: 
     * @exception: 
    */
    public void getChildren(String path){
        try {
            List<String> children = zooKeeper.getChildren(path, false);
            for(String c : children)
                System.out.println(c);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

    @Deprecated
    public void connect(String connectString) {
        try {
            this.zooKeeper = new ZooKeeper(connectString, 50000, new ConnectWatcher());
            String result = zooKeeper.create("/test", null,ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
            System.out.println(result);
            this.setAuth();
            Stat stat = zooKeeper.setACL(result, setAcl(), -1);

            log.info(Stat.signature());
            zooKeeper.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.close();
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

    public List<ACL> setAcl(){
        List<ACL> result = new ArrayList<ACL>();
        Id id = new Id();
        id.setScheme("digest");
        id.setId("username:+Ir5sN1lGJEEs8xBZhZXKvjLJ7c=");
        ACL acl = new ACL();
        acl.setId(id);
        acl.setPerms(ZooDefs.Perms.READ | ZooDefs.Perms.WRITE);

        result.add(acl);
        return result;
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
