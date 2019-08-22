package com.yujun.zookeeper.base;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Zookeeper连接配置类
 * @author admin
 * @version 1.0.0
 * @date 2019/8/22 10:09
 * @description Zookeeper连接配置类
 **/
@Getter
@Setter
@NoArgsConstructor
public class ZookeeperConnectConfig {

    /** zookeeper host:port **/
    private String connectString;
    /** client session timeout **/
    private int sessionTimeout;

}
