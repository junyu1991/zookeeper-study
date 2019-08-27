package com.yujun.zookeeper.base.lock;

import com.yujun.zookeeper.base.Const;
import com.yujun.zookeeper.base.ZookeeperConnectConfig;
import com.yujun.zookeeper.base.ZookeeperConnector;
import com.yujun.zookeeper.exception.ZookeeperLockException;
import com.yujun.zookeeper.util.ZookeeperCompartor;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

import java.util.List;

/**
 * @author admin
 * @version 1.0.0
 * @date 2019/8/27 15:37
 * @description TODO
 **/
public abstract class ZookeeperBaseLock implements ZookeeperLock {

    private final ZookeeperConnector zookeeperConnector;

    protected ZookeeperBaseLock(ZookeeperConnector zookeeperConnector) {
        this.zookeeperConnector = zookeeperConnector;
    }

    /**
     * 使用ZookeeperConnectConfig初始化zookeeperconnector连接类
     * @author: yujun
     * @date: 2019/8/26
     * @description: TODO
     * @param config
     * @return:
     * @exception:
     */
    public ZookeeperBaseLock(ZookeeperConnectConfig config) {
        this.zookeeperConnector = ZookeeperConnector.getInstance(config);
    }

    /**
     * 创建zookeeper path
     * @author: yujun
     * @date: 2019/8/26
     * @description: TODO
     * @param node
     * @param data
     * @return: {@link java.lang.String}
     * @exception:
     */
    protected String createNode(String node, byte[] data) throws ZookeeperLockException, KeeperException, InterruptedException {
        if(this.zookeeperConnector == null) {
            throw new ZookeeperLockException("ZookeeperConnector is null, please call setZookeeperConnector() method to set ZookeeperConnector parameter");
        }
        String path = this.zookeeperConnector.createNodeIfNotExists(node, CreateMode.EPHEMERAL_SEQUENTIAL, ZooDefs.Ids.OPEN_ACL_UNSAFE, data);
        return path;
    }

    /**
     * 获取指定path下的所有子节点
     * @author: yujun
     * @date: 2019/8/26
     * @description: TODO
     * @param path
     * @return: {@link java.util.List<java.lang.String>}
     * @exception:
     */
    protected List<String> getChildren(String path) throws ZookeeperLockException, KeeperException, InterruptedException {
        if(this.zookeeperConnector == null) {
            throw new ZookeeperLockException("ZookeeperConnector is null, please call setZookeeperConnector() method to set ZookeeperConnector parameter");
        }
        return this.zookeeperConnector.getChildren(path);
    }

    /**
     * 从指定的路径下获取相应递增节点的下一小节点
     * @author: yujun
     * @date: 2019/8/26
     * @description: TODO
     * @param path
     * @param nodeName
     * @param prefix
     * @return: {@link java.lang.String}
     * @exception:
     */
    protected String getNextLowerNode(String path, String nodeName, String prefix) throws InterruptedException, KeeperException, ZookeeperLockException {
        List<String> children = this.getChildren(path);
        children.sort(new ZookeeperCompartor());
        long max = Long.parseLong(nodeName.substring(nodeName.length()-10, nodeName.length()));
        String lowerNode = null;
        for(String p : children) {
            if(p.length() <= 10)
                continue;
            if(!p.startsWith(prefix))
                continue;
            long temp = Long.parseLong(p.substring(p.length()-10, p.length()));
            if(temp >= max)
                continue;
            if(temp < max) {
                lowerNode = path + Const.ZOOKEEPERSEPRITE + p;
                break;
            }
        }
        return lowerNode;
    }


    /**
     * 给指定节点设置watch事件
     * @author: yujun
     * @date: 2019/8/26
     * @description: TODO
     * @param path
     * @param readObject
     * @return: {@link org.apache.zookeeper.data.Stat}
     * @exception:
     */
    protected Stat exists(String path, Object readObject) throws KeeperException, InterruptedException, ZookeeperLockException {
        if(this.zookeeperConnector == null) {
            throw new ZookeeperLockException("ZookeeperConnector is null, please call setZookeeperConnector() method to set ZookeeperConnector parameter");
        }
        LockWatcher lockWatcher = new LockWatcher(readObject, path);
        System.out.println("Set watcher : " + path);
        Stat exists = zookeeperConnector.getZooKeeper().exists(path, lockWatcher);
        return exists;
    }

    /**
     *  删除指定节点
     * @author: yujun
     * @date: 2019/8/26
     * @description: TODO
     * @param path
     * @return:
     * @exception:
     */
    protected void deletePath(String path) throws ZookeeperLockException, KeeperException, InterruptedException {
        if(this.zookeeperConnector == null) {
            throw new ZookeeperLockException("ZookeeperConnector is null, please call setZookeeperConnector() method to set ZookeeperConnector parameter");
        }
        if(this.zookeeperConnector.getZooKeeper().exists(path, false) != null)
            this.zookeeperConnector.getZooKeeper().delete(path, -1);
    }
}
