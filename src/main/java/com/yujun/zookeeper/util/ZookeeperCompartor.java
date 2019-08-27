package com.yujun.zookeeper.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author admin
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
