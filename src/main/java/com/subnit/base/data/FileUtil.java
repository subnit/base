package com.subnit.base.data;

import org.apache.maven.shared.invoker.*;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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

    public static List<String> listAllFilePath(String path) {
        List<String> res = new ArrayList<>();
        listAllFilePath(path, res);
        return res;
    }

    private static List<String> listAllFilePath(String path, List<String> res) {
        File file = new File(path);
        File[] files = file.listFiles();
        if (files == null) {
            return res;
        }

        for (File f : files) {
            if (f.isDirectory()) {
                res.add(f.getPath());
                listAllFilePath(f.getPath(), res);
            } else {
                res.add(file.getPath());
            }
        }
        return res;
    }





    public static void unzipJar(String destinationDir, String jarPath) throws IOException {
        File file = new File(jarPath);
        JarFile jarFile = new JarFile(file);

        for (Enumeration<JarEntry> enums = jarFile.entries(); enums.hasMoreElements();) {
            JarEntry entry = enums.nextElement();
            if (!entry.getName().startsWith("BOOT-INF/lib")) {
                continue;
            }
            String[] split = entry.getName().split("/");
            String fileName = destinationDir + File.separator + split[split.length - 1];
            File f = new File(fileName);
            if (!fileName.endsWith("/")) {
                InputStream is = jarFile.getInputStream(entry);
                ReadableByteChannel inChannel = Channels.newChannel(is);
                FileOutputStream fos = new FileOutputStream(f);
                FileChannel outChannel = fos.getChannel();
                ByteBuffer byteBuffer = ByteBuffer.allocate(1000);
                int length = inChannel.read(byteBuffer);
                while (length != -1) {
                    byteBuffer.flip();
                    outChannel.write(byteBuffer);
                    byteBuffer.clear();
                    length = inChannel.read(byteBuffer);
                }
                is.close();
                outChannel.close();
                fos.close();
                inChannel.close();
            }
        }
    }

    public static void operationMavenOrder(String mavenPath, String pomPath, String mavenOrder) throws MavenInvocationException {
        InvocationRequest request = new DefaultInvocationRequest();
        // 想要操控的pom文件的位置
        request.setPomFile(new File(pomPath));
        // 操控的maven命令
        request.setGoals(Collections.singletonList(mavenOrder));
        Invoker invoker = new DefaultInvoker();
        // maven的位置
        invoker.setMavenHome(new File(mavenPath));
        invoker.setLogger(new PrintStreamLogger(System.err, InvokerLogger.ERROR) {
        });
        invoker.setOutputHandler(new InvocationOutputHandler() {
            @Override
            public void consumeLine(String s) throws IOException {

            }
        });
        invoker.execute(request);
    }

    public static void main(String[] args) throws IOException {
        unzipJar("/Users/huihui/IdeaProjects/jarTemp",
                "/Users/huihui/IdeaProjects/subnit-web/subnit-web-start/target/subnit-web-start-0.0.1-SNAPSHOT.jar");
    }



}
