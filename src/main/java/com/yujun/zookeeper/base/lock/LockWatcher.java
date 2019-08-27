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

    private Object waitObject;
    private String watchPath;
    public LockWatcher(Object waitObject, String watchPath) {
        this.waitObject = waitObject;
        this.watchPath = watchPath;
    }

    @Override
    public void process(WatchedEvent event) {
        Event.EventType type = event.getType();
        if(event.getPath() == watchPath && type == Event.EventType.NodeDeleted) {
            log.debug(event.getPath() + " deleted");
            waitObject.notify();
        }
    }
}
