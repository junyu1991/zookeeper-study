package com.yujun.zookeeper.util;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * @author admin
 * @version 1.0.0
 * @date 2019/8/23 14:57
 * @description TODO
 **/
public class EncryptUtil {
    static final String HEXES = "0123456789ABCDEF";

    static String getHex(byte[] raw) {
        final StringBuilder hex = new StringBuilder();
        for (int i=0; i < raw.length; i++) {
            hex.append(Integer.toString( ( raw[i] & 0xff ) + 0x100, 16).substring( 1 ));
        }
        return hex.toString();
    }

    /**
     * 计算字符串text的sha1值
     * @author: admin
     * @date: 2019/8/23
     * @description: TODO
     * @param text
     * @return: {@link String}
     * @exception:
    */
    public static String encodeSha1(String text) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("sha1");
            messageDigest.update(text.getBytes());
            return getHex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** 计算content的sha1值
     * @author: admin
     * @date: 2019/8/23
     * @description: TODO 
     * @param content
     * @return: {@link String}
     * @exception: 
    */
    public static String encodeSha1(byte[] content) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("sha1");
            messageDigest.update(content);
            return getHex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 计算字符串plainText的base64编码值
     * @author: admin
     * @date: 2019/8/23
     * @description: TODO
     * @param plainText
     * @return: {@link String}
     * @exception:
    */
    public static String getBase64String(String plainText) {
        return Base64.getEncoder().encodeToString(plainText.getBytes());
    }

    /**
     * 对content进行base64编码
     * @author: admin
     * @date: 2019/8/23
     * @description: TODO
     * @param content
     * @return: {@link String}
     * @exception:
    */
    public static String getBase64String(byte[] content) {
        return Base64.getEncoder().encodeToString(content);
    }

    /**
     * 按照指定算法计算content的消息摘要，返回的是二进制编码
     * @author: admin
     * @date: 2019/8/23
     * @description: TODO
     * @param content
     * @param algorithm
     * @return: {@link byte[]}
     * @exception:
    */
    public static byte[] digest(byte []content, String algorithm) throws NoSuchAlgorithmException {
        MessageDigest instance = MessageDigest.getInstance(algorithm);
        instance.update(content);
        return instance.digest();
    }

    /**
     * 计算字符串content的sha1消息摘要
     * @author: admin
     * @date: 2019/8/23
     * @description: TODO
     * @param content 需要计算sha1消息摘要的字符串
     * @return: {@link byte[]}
     * @exception:
    */
    public static byte[] digestSha1(String content) throws NoSuchAlgorithmException {
        return digest(content.getBytes(), "sha1");
    }

    @Deprecated
    public static String digest(String content) throws NoSuchAlgorithmException {
        MessageDigest instance = MessageDigest.getInstance("sha1");
        instance.update(content.getBytes());
        return DatatypeConverter.printHexBinary(instance.digest());
    }
}
