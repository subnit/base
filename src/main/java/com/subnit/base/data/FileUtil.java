package com.subnit.base.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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

    public static List<String> readTxtByLine(String filePath) {
        List<String> res = new ArrayList<>();
        FileInputStream fis = null;
        FileChannel inChannel = null;
        int bufSize = 1024*10;
        try {
            fis = new FileInputStream(filePath);
            inChannel = fis.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(bufSize);
            String enterStr = "\n";
            StringBuilder strBuf = new StringBuilder("");
            while(inChannel.read(buffer) != -1){
                int rSize = buffer.position();
                buffer.clear();
                String tempString = new String(buffer.array(), 0, rSize);
                if(fis.available() ==0){
                    tempString+="\n";
                }

                int fromIndex = 0;

                int endIndex;
                while ((endIndex = tempString.indexOf(enterStr, fromIndex)) != -1) {
                    String line = tempString.substring(fromIndex, endIndex);
                    line = strBuf.toString() + line;
                    strBuf.delete(0, strBuf.length());
                    fromIndex = endIndex + 1;
                    res.add(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(inChannel != null){
                try{
                    inChannel.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        return res;
    }

    /**
     *  get value from properties file
     * @param key key
     * @param path properties file payh
     * @return value
     */
    public static String getSourcingValueBykey(String key, String path) {
        String value = "";
        try {
            FileInputStream inputFile = new FileInputStream(path);
            Properties properties = new Properties();
            properties.load(inputFile);
            inputFile.close();
            value = properties.getProperty(key);
            if (DataUtil.isEmpty(value)) {
                return value;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }


    public static void main(String[] args) {
        System.out.println(System.getProperty("user.dir"));
        String filePath = "src/main/resources/token.properties";
        System.out.println(filePath);
        System.out.println(getSourcingValueBykey("username",filePath));
    }



}
