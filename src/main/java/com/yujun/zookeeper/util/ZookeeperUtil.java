package com.yujun.zookeeper.util;


import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;

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
     * @param expression 用于生成秘文的字符串，格式例子： username:password
     * @return: {@link String} 可直接用于acl中的id的字符串。如： username:+Ir5sN1lGJEEs8xBZhZXKvjLJ7c=
     * @exception:
    */
    public static String getDigestString(String expression) throws InvalidParameterException {
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

    /**
     * 获取使用digest控制的acl
     * @author: hunter
     * @date: 8/23/19
     * @description: TODO
     * @param username 用户名
     * @param password 用户密码
     * @param permission 节点权限
     * @return: {@link ACL}
     * @exception:
    */
    public static ACL getAcl(String username, String password, int permission){
        Id ids = new Id();
        ids.setScheme(Const.DIGEST);
        ids.setId(getDigestString(username + ":" +password));
        ACL acl = new ACL();
        acl.setId(ids);
        acl.setPerms(permission);
        return acl;
    }


}
