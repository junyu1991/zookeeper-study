package com.yujun.zookeeper.base;

/**
 * @author admin
 * @version 1.0.0
 * @date 2019/8/22 17:33
 * @description TODO
 **/
public class Const {
    /** 分布式读锁path **/
    public static final String READLOCK = "/lock/read-";
    /** 分布式写锁path **/
    public static final String WRITELOCK = "/lock/write-";

    /**  **/
    public static final String LOCK = "/lock";
}
