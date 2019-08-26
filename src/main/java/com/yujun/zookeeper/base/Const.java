package com.yujun.zookeeper.base;

/**
 * @author admin
 * @version 1.0.0
 * @date 2019/8/22 17:33
 * @description TODO
 **/
public class Const {
    /** 分布式读锁path **/
    public static final String READLOCK = "/lock/_locknoe_/read-";
    /** 分布式写锁path **/
    public static final String WRITELOCK = "/lock/_locknode_/write-";
    /** 分布式锁path **/
    public static final String LOCK = "/lock/_locknode_/lock-";

    /** Zookeeper目录分隔符 **/
    public static final String ZOOKEEPERSEPRITE = "/";

}
