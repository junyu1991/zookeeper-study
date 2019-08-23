package com.yujun.zookeeper.util;


import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;

/**
 * @author admin
 * @version 1.0.0
 * @date 2019/8/23 17:05
 * @description TODO
 **/
public class ZookeeperUtil {

    /**
     * 计算zookeeper设置acl使用digest时需要使用的加密串
     * @author: admin
     * @date: 2019/8/23
     * @description: TODO
     * @param expression
     * @return: {@link String}
     * @exception:
    */
    public static String getDigestString(String expression) {
        String[] split = expression.split(":");
        StringBuilder result = new StringBuilder();
        if(split.length != 2)
            throw new InvalidParameterException("expression is illegal,expression should look like username:password");
        try {
            String encrypt = EncryptUtil.getBase64String(EncryptUtil.digestSha1(expression));
            result.append(split[0]).append(":");
            result.append(encrypt);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

}
