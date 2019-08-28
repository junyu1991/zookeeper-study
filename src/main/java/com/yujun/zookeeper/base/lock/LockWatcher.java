package com.yujun.zookeeper.base.lock;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

/**
 * @author admin
 * @version 1.0.0
 * @date 2019/8/26 17:17
 * @description TODO
 **/
@Slf4j
public class LockWatcher implements Watcher {

    private ZookeeperWaitObject waitObject;
    private String watchPath;
    public LockWatcher(ZookeeperWaitObject waitObject, String watchPath) {
        this.waitObject = waitObject;
        this.watchPath = watchPath;
    }

    @Override
    public void process(WatchedEvent event) {
        Event.EventType type = event.getType();
        System.out.println("Watcher " + watchPath);
        if(event.getPath().equals(watchPath) && type == Event.EventType.NodeDeleted) {
            System.out.println(event.getPath() + " deleted");
            log.debug(event.getPath() + " deleted");
            synchronized (waitObject) {
                waitObject.setNotified(true);
                System.out.println(event.getPath() + " notify others");
                waitObject.notify();
            }
        }
    }
}
