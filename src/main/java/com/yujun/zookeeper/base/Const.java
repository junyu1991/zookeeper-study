package com.yujun.zookeeper.base;

/**
 * @author admin
 * @version 1.0.0
 * @date 2019/8/22 17:33
 * @description TODO
 **/
public class Const {
    /** 分布式读写锁path **/
    public static final String READWRITELOCK = "/lock/_locknoe_/rw";

    /** 读锁前缀 **/
    public static final String READ = "read-";

    /** 写锁前缀 **/
    public static final String WRITE = "write-";

    /** 分布式锁path **/
    public static final String LOCK = "/lock/_locknode_/lock-";

    /** 分布式时序锁path **/
    public static final String SEQUENTIALLOCK = "/lock/_locknode_/sequential";

    /** Zookeeper目录分隔符 **/
    public static final String ZOOKEEPERSEPRITE = "/";

}
