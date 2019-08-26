package com.yujun.zookeeper.base.lock;

import com.yujun.zookeeper.base.Const;
import com.yujun.zookeeper.base.ZookeeperConnectConfig;
import com.yujun.zookeeper.base.ZookeeperConnector;
import com.yujun.zookeeper.exception.ZookeeperLockException;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

import java.util.List;
public enum ZookeeperLock {
    READLOCK {
        /** 用于线程同步的Object **/
        private Object readObject = new Object();
        /**  **/
        private String readLockString;
        /**  **/
        private String writeLockString;
        /**
         * 请求分布式锁
         *
         * @param lockString
         * @author: yujun
         * @date: 2019/8/26
         * @description: TODO
         * @return:
         * @exception:
         */
        @Override
        public void lock(String lockString) throws InterruptedException, KeeperException, ZookeeperLockException {
            this.readLockString = Const.READLOCK + Const.ZOOKEEPERSEPRITE + lockString;
            this.writeLockString = Const.WRITELOCK + Const.ZOOKEEPERSEPRITE + lockString;
            String nodeName = createNode(readLockString, null);
            String writeNode = getNextLowerNode(writeLockString, lockString);
            while(nodeName != writeNode) {
                if(exists(writeNode, readObject) != null) {
                    readObject.wait();
                    writeNode = getNextLowerNode(writeLockString, lockString);
                } else {
                    writeNode = getNextLowerNode(writeLockString, lockString);
                }
            }
        }

        /**
         * 释放当前占用的分布式锁
         *
         * @param lockString
         * @author: yujun
         * @date: 2019/8/26
         * @description: TODO
         * @return:
         * @exception:
         */
        @Override
        public void release(String lockString) throws ZookeeperLockException, KeeperException, InterruptedException {
            deletePath(Const.READLOCK + Const.ZOOKEEPERSEPRITE + lockString);
        }

        /**
         * @author: yujun
         * @date: 2019/8/26
         * @description: TODO
         * @return: {@link String}
         * @exception:
         */
        @Override
        public String getLockString() {
            return null;
        }
        
    }, WRITELOCK {
        /**
         * 请求分布式锁
         *
         * @param lockString
         * @author: yujun
         * @date: 2019/8/26
         * @description: TODO
         * @return:
         * @exception:
         */
        @Override
        public void lock(String lockString) {

        }

        /**
         * 释放当前占用的分布式锁
         *
         * @param lockString
         * @author: yujun
         * @date: 2019/8/26
         * @description: TODO
         * @return:
         * @exception:
         */
        @Override
        public void release(String lockString) {

        }

        /**
         * @author: yujun
         * @date: 2019/8/26
         * @description: TODO
         * @return: {@link String}
         * @exception:
         */
        @Override
        public String getLockString() {
            return null;
        }

        
    }, LOCK {
        /**
         * 请求分布式锁
         *
         * @param lockString
         * @author: yujun
         * @date: 2019/8/26
         * @description: TODO
         * @return:
         * @exception:
         */
        @Override
        public void lock(String lockString) {

        }

        /**
         * 释放当前占用的分布式锁
         *
         * @param lockString
         * @author: yujun
         * @date: 2019/8/26
         * @description: TODO
         * @return:
         * @exception:
         */
        @Override
        public void release(String lockString) {

        }

        /**
         * @author: yujun
         * @date: 2019/8/26
         * @description: TODO
         * @return: {@link String}
         * @exception:
         */
        @Override
        public String getLockString() {
            return null;
        }

        private String lockString = "";
        
    };
    
    /** 
     * 请求分布式锁
     * @author: yujun
     * @date: 2019/8/26
     * @description: TODO 
     * @param lockString
     * @return: 
     * @exception: 
    */
    public abstract void lock(String lockString) throws InterruptedException, KeeperException, ZookeeperLockException;
    
    /** 
     * 释放当前占用的分布式锁
     * @author: yujun
     * @date: 2019/8/26
     * @description: TODO 
     * @param lockString
     * @return: 
     * @exception: 
    */
    public abstract void release(String lockString) throws InterruptedException, KeeperException, ZookeeperLockException;
    
    /** 
     * 
     * @author: yujun
     * @date: 2019/8/26
     * @description: TODO 
     * @param 
     * @return: {@link java.lang.String}
     * @exception: 
    */
    public abstract String getLockString();

    private ZookeeperConnector zookeeperConnector = null;
    
    /** 
     * 初始化zookeeperconnector连接类
     * @author: yujun
     * @date: 2019/8/26
     * @description: TODO 
     * @param zookeeperConnector
     * @return: 
     * @exception: 
    */
    public void setZookeeperConnector(ZookeeperConnector zookeeperConnector) {
        this.zookeeperConnector = zookeeperConnector;
    }
    
    /** 
     * 初始化zookeeperconnector连接类
     * @author: yujun
     * @date: 2019/8/26
     * @description: TODO 
     * @param config
     * @return: 
     * @exception: 
    */
    public void setZookeeperConnector(ZookeeperConnectConfig config) {
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
    public String createNode(String node, byte[] data) throws ZookeeperLockException, KeeperException, InterruptedException {
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
    public List<String> getChildren(String path) throws ZookeeperLockException, KeeperException, InterruptedException {
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
     * @return: {@link java.lang.String}
     * @exception: 
    */
    public String getNextLowerNode(String path, String nodeName) throws InterruptedException, KeeperException, ZookeeperLockException {
        List<String> children = this.getChildren(path);
        String lockName = nodeName.substring(0, nodeName.length()-10);
        long max = Long.parseLong(nodeName.substring(nodeName.length()-10, nodeName.length()));
        if(max == 0) {
            return nodeName;
        }
        String lowerNode = nodeName;
        for(String p : children) {
            if(p.length() == nodeName.length()) {
                if(p.startsWith(lockName)) {
                    long l = Long.parseLong(p.substring(lockName.length(), p.length()));
                    if(l == max-1) {
                        lowerNode = p;
                        break;
                    }
                }
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
    public Stat exists(String path, Object readObject) throws KeeperException, InterruptedException, ZookeeperLockException {
        if(this.zookeeperConnector == null) {
            throw new ZookeeperLockException("ZookeeperConnector is null, please call setZookeeperConnector() method to set ZookeeperConnector parameter");
        }
        LockWatcher lockWatcher = new LockWatcher(readObject, path);
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
    public void deletePath(String path) throws ZookeeperLockException, KeeperException, InterruptedException {
        if(this.zookeeperConnector == null) {
            throw new ZookeeperLockException("ZookeeperConnector is null, please call setZookeeperConnector() method to set ZookeeperConnector parameter");
        }
        this.zookeeperConnector.getZooKeeper().delete(path, -1);
    }

}
