package com.wp.utils;

/**
 * @author 王萍
 * @date 2018/2/7 0007
 */
public class StringUtil {

    /**
     * 判断字符串是否非空
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 判断字符串是否为空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
}
