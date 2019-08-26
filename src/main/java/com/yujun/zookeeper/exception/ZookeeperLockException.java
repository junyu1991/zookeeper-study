package com.yujun.zookeeper.exception;

/**
 * @author admin
 * @version 1.0.0
 * @date 2019/8/26 14:31
 * @description TODO
 **/
public class ZookeeperLockException extends Exception {

    public ZookeeperLockException(){
        super();
    }
    public ZookeeperLockException(String message) {
        super(message);
    }
    public ZookeeperLockException(Throwable throwable) {
        super(throwable);
    }
    public ZookeeperLockException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
