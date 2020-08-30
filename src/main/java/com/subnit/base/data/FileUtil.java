package com.subnit.base.data;

import java.io.File;

/**
 * description:
 * date : create in 下午10:21 2020/8/29
 * modified by :
 *
 * @author subo
 */
public class FileUtil {



    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String child : children) {
                    boolean success = deleteDir(new File(dir, child));
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        return dir.delete();
    }

}
