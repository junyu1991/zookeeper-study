package com.yujun.zookeeper.exception;

/**
 * 未释放锁资源异常，通常在未释放之前申请的锁而又重复申请锁的情况下被抛出
 * @author yujun
 * @version 1.0.0
 * @date 2019/9/2 11:35
 * @description TODO
 **/
public class ZookeeperLockUnreleaseException extends Exception {
    public ZookeeperLockUnreleaseException() {
        super();
    }
    public ZookeeperLockUnreleaseException(String message) {
        super(message);
    }

    public ZookeeperLockUnreleaseException(Throwable t) {
        super(t);
    }

    public ZookeeperLockUnreleaseException(String message, Throwable t) {
        super(message, t);
    }
}
