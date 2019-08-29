package com.yujun.zookeeper.util;

import java.util.Comparator;

/**
 * 有序节点排序类
 * @author yujun
 * @version 1.0.0
 * @date 2019/8/27 13:34
 * @description TODO
 **/
public class ZookeeperCompartor implements Comparator<String> {

    @Override
    public int compare(String o1, String o2) {
        if(o1.length()>10 && o2.length()>10) {
            return Long.parseLong(o1.substring(o1.length()-10, o1.length())) > Long.parseLong(o2.substring(o2.length()-10,o2.length())) ? -1 : 1;
        }
        return -1;
    }
}
