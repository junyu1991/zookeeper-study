package com.yujun.zookeeper.base;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yujun
 * @version 1.0.0
 * @date 2019/9/3 14:35
 * @description TODO
 **/
public class ZookeeperConnectorPool {

    private final Map<ZookeeperConnectConfig, ZookeeperConnector> map;
    private int poolSize = 100;
    private volatile static ZookeeperConnectorPool pool;

    private ZookeeperConnectorPool(int poolSize) {
        this.map = new ConcurrentHashMap<ZookeeperConnectConfig, ZookeeperConnector>(poolSize);
        this.poolSize = poolSize;
    }

    private ZookeeperConnectorPool() {
        this.map = new ConcurrentHashMap<ZookeeperConnectConfig, ZookeeperConnector>(poolSize);
    }

    public static ZookeeperConnectorPool getConnectorPool(){
        synchronized (ZookeeperConnectorPool.class) {
            if(pool == null)
                pool = new ZookeeperConnectorPool();
            return pool;
        }
    }
    public static ZookeeperConnectorPool getConnectorPool(int poolSize){
        synchronized (ZookeeperConnectorPool.class) {
            if(pool == null)
                pool = new ZookeeperConnectorPool(poolSize);
            return pool;
        }
    }

    public void addConnector(ZookeeperConnectConfig config, ZookeeperConnector connector) {
        map.put(config, connector);
    }

    public ZookeeperConnector getConnector(ZookeeperConnectConfig config){
        return map.get(config);
    }

    public ZookeeperConnector deleteConnector(ZookeeperConnectConfig config) {
        return map.remove(config);
    }

    public boolean deleteConnector(ZookeeperConnectConfig config, ZookeeperConnector connector){
        return map.remove(config, connector);
    }

    public boolean containerConnctor(ZookeeperConnector connector) {
        return map.containsValue(connector);
    }

    public boolean containerConnctor(ZookeeperConnectConfig config) {
        return map.containsKey(config);
    }

}
