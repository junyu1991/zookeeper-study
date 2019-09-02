package com.yujun.zookeeper.base.lock;

import com.yujun.zookeeper.base.Const;
import com.yujun.zookeeper.base.ZookeeperConnectConfig;
import com.yujun.zookeeper.base.ZookeeperConnector;
import com.yujun.zookeeper.exception.ZookeeperLockException;
import com.yujun.zookeeper.exception.ZookeeperLockUnreleaseException;
import com.yujun.zookeeper.util.ZookeeperCompartor;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * @author admin
 * @version 1.0.0
 * @date 2019/8/27 15:37
 * @description TODO
 **/
@Slf4j
public abstract class ZookeeperBaseLock implements ZookeeperLock {

    private final ZookeeperConnector zookeeperConnector;
    protected final String lockString;

    /**
     * 使用ZookeeperConnectConfig初始化zookeeperconnector连接类
     * @author: yujun
     * @date: 2019/8/26
     * @description: TODO
     * @param config
     * @param lockString
     * @return:
     * @exception:
     */
    protected ZookeeperBaseLock(ZookeeperConnectConfig config, String lockString) {
        this.zookeeperConnector = ZookeeperConnector.getInstance(config);
        this.lockString = lockString;
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
            throw new ZookeeperLockException("The ZookeeperConnector is not initialized.");
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
            throw new ZookeeperLockException("The ZookeeperConnector is not initialized.");
        }
        return this.zookeeperConnector.getChildren(path);
    }

    /**
     * 从指定的路径下获取相应递增节点的下一小节点，需要指定节点前缀
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
     * 从指定的路径下获取相应递增节点的下一小节点
     * @author: yujun
     * @date: 2019/8/28
     * @description: TODO
     * @param path
     * @param nodeName
     * @return: {@link String}
     * @exception:
    */
    protected String getNextLowerNode(String path, String nodeName) throws InterruptedException, KeeperException, ZookeeperLockException {
        List<String> children = this.getChildren(path);
        children.sort(new ZookeeperCompartor());
        long max = Long.parseLong(nodeName.substring(nodeName.length()-10, nodeName.length()));
        String lowerNode = null;
        for(String p : children) {
            if(p.length() <= 10)
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
     * @param blockingQueue
     * @return: {@link org.apache.zookeeper.data.Stat}
     * @exception:
     */
    protected Stat exists(String path, BlockingQueue<String> blockingQueue) throws KeeperException, InterruptedException, ZookeeperLockException {
        if(this.zookeeperConnector == null) {
            throw new ZookeeperLockException("The ZookeeperConnector is not initialized.");
        }
        LockWatcher lockWatcher = new LockWatcher(path, blockingQueue);
        log.info("Set watcher : " + path);
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
            throw new ZookeeperLockException("The ZookeeperConnector is not initialized.");
        }
        if(this.zookeeperConnector.getZooKeeper().exists(path, false) != null)
            this.zookeeperConnector.getZooKeeper().delete(path, -1);
    }

    /**
     * 创建临时节点
     * @author: yujun
     * @date: 2019/8/29
     * @description: TODO
     * @param node
     * @param data
     * @return: {@link String}
     * @exception:
    */
    protected String createEphemeralNode(String node, byte[] data) throws ZookeeperLockException, KeeperException, InterruptedException {
        if(this.zookeeperConnector == null) {
            throw new ZookeeperLockException("The ZookeeperConnector is not initialized.");
        }
        String path = this.zookeeperConnector.createNodeIfNotExists(node, CreateMode.EPHEMERAL, ZooDefs.Ids.OPEN_ACL_UNSAFE, data);
        return path;
    }

    /** 
     * 创建永久节点
     * @author: yujun
     * @date: 2019/8/29
     * @description: TODO 
     * @param node
     * @param data
     * @return: {@link String}
     * @exception: 
    */
    protected String createPersistentNode(String node, byte[] data) throws ZookeeperLockException, KeeperException, InterruptedException {
        if(this.zookeeperConnector == null) {
            throw new ZookeeperLockException("The ZookeeperConnector is not initialized.");
        }
        String path = this.zookeeperConnector.createNodeIfNotExists(node, CreateMode.PERSISTENT, ZooDefs.Ids.OPEN_ACL_UNSAFE, data);
        return path;
    }

    /** 
     * 检测当前锁是否有未释放的锁资源，若有抛出ZookeeperLockUnreleaseException
     * @author: yujun
     * @date: 2019/9/2
     * @description: TODO 
     * @param 
     * @return: 
     * @exception: ZookeeperLockUnreleaseException 存在未释放的锁资源
    */
    protected void checkedLockRelease() throws ZookeeperLockUnreleaseException {
        Thread t = Thread.currentThread();
        if(LockContainer.containLock(t)) {
            throw new ZookeeperLockUnreleaseException("The current thread has unrelease lock [" + LockContainer.getLockString(t)+"]");
        }
    }

    @Override
    public void realease() throws InterruptedException, KeeperException, ZookeeperLockException {
        if(LockContainer.getLockString(Thread.currentThread()) != null) {
            this.deletePath(LockContainer.getLockStringRemoved(Thread.currentThread()));
        }
    }

    @Override
    public String getLockString() {
        return LockContainer.getLockString(Thread.currentThread());
    }
}
