package com.yujun.zookeeper.base;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author admin
 * @version 1.0.0
 * @date 2019/8/21 16:52
 * @description TODO
 **/
@Slf4j
public class Connector {

    private String connectString = "172.18.1.109:2181";
    private ZooKeeper zooKeeper = null;

    public Connector(String connectString){
        this.init(connectString);
    }


    private void init(String connectString){
        try {
            this.zooKeeper = new ZooKeeper(connectString, 50000, new ConnectWatcher());
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

    @Deprecated
    public void connect(){
        this.connect(connectString);
    }
}

class ConnectWatcher implements Watcher {
    public void process(WatchedEvent watchedEvent) {

    }
}
