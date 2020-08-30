package com.subnit.base.data;

import java.util.Collection;

/**
 * description: data util
 *
 * @author subo177693
 */
public class DataUtil {

    public static boolean isEmpty(String... str) {
        for (String s : str) {
            if (isEmpty(s)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isEmpty(Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> coll) {
        return !isEmpty(coll);
    }
}
