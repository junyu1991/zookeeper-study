package com.yujun.zookeeper.base;

import com.yujun.zookeeper.util.Const;
import com.yujun.zookeeper.util.ZookeeperUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
            //this.setAuth();
            Stat stat = zooKeeper.setACL(result, setAcl(), -1);

            log.info(Stat.signature());
            zooKeeper.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.close();
        }
    }

    /**
     * 关闭当前zookeeper连接
     * @author: hunter
     * @date: 8/24/19
     * @description: TODO
     * @param
     * @return:
     * @exception:
    */
    public void close(){
        if(this.zooKeeper != null) {
            try {
                this.zooKeeper.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 给指定path设置digest ACL
     * @author: hunter
     * @date: 8/24/19
     * @description: TODO
     * @param path
     * @param username
     * @param password
     * @param permisssion
     * @return:
     * @exception:
    */
    public void setAcl(String path, String username, String password, int permisssion) throws KeeperException, InterruptedException {
        if(this.zooKeeper != null) {
            if(this.zooKeeper.exists(path, false) != null) {
                ACL acl = ZookeeperUtil.getAcl(username, password, permisssion);
                List<ACL> acls = new ArrayList<ACL>();
                acls.add(acl);
                zooKeeper.setACL(path, acls, -1);
            }
        }
    }

    /**
     * 给当前zookeeper连接添加auth info
     * @author: hunter
     * @date: 8/24/19
     * @description: TODO
     * @param username
     * @param password
     * @return:
     * @exception:
    */
    public void setDigestAuth(String username, String password){
        if(this.zooKeeper != null) {
            zooKeeper.addAuthInfo(Const.DIGEST, new String(username+":"+password).getBytes());
        }
    }

    /**
     * @author: admin
     * @date: 2019/8/23
     * @description: A method to test setAcl
     * @param
     * @return:
     * @exception:
    */
    public void testAcl(String path){
        try {
            if(zooKeeper.exists(path,false) == null) {
                zooKeeper.create(path,null,ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            zooKeeper.setACL(path, setAcl(), -1);
            zooKeeper.close();
        } catch (Exception e) {
            e.printStackTrace();
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
    public String createNodeIfNotExists(String path) throws Exception {
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
    public String createNodeIfNotExists(String path, CreateMode createMode, List<ACL> acl, byte[] data) throws KeeperException, InterruptedException {
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



    public List<ACL> setAcl(){
        List<ACL> result = new ArrayList<ACL>();
        Id id = new Id();
        ACL acl = new ACL();

        id.setScheme("auth");
        id.setId("test:test");
        acl.setId(id);
        acl.setPerms(ZooDefs.Perms.READ);
        result.add(acl);

        id.setScheme("digest");
        id.setId("username:+Ir5sN1lGJEEs8xBZhZXKvjLJ7c=");

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
