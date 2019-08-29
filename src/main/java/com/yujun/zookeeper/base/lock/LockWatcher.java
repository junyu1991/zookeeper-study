package com.yujun.zookeeper.base.lock;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.BlockingQueue;

/**
 * @author admin
 * @version 1.0.0
 * @date 2019/8/26 17:17
 * @description TODO
 **/
@Slf4j
public class LockWatcher implements Watcher {

    //private ZookeeperWaitObject waitObject;
    private BlockingQueue<String> blockingQueue;
    private String watchPath;
    public LockWatcher(String watchPath, BlockingQueue<String> blockingQueue) {
        //this.waitObject = waitObject;
        this.watchPath = watchPath;
        this.blockingQueue = blockingQueue;
    }

    @Override
    public void process(WatchedEvent event) {
        Event.EventType type = event.getType();
        System.out.println("Watcher " + watchPath);
        if(event.getPath().equals(watchPath) && type == Event.EventType.NodeDeleted) {
            System.out.println(event.getPath() + " deleted");
            log.debug(event.getPath() + " deleted");
            blockingQueue.offer(event.getPath());
            /*synchronized (waitObject) {
                waitObject.setNotified(true);
                System.out.println(event.getPath() + " notify others");
                waitObject.notify();
            }*/
        }
    }
}
