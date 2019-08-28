package com.yujun.zookeeper.base.lock;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author admin
 * @version 1.0.0
 * @date 2019/8/28 14:26
 * @description TODO
 **/
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ZookeeperWaitObject {
    private boolean notified = false;
}
