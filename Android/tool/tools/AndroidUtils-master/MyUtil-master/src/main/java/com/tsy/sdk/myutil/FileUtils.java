package com.tsy.sdk.myutil;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * File工具类
 * Created by tsy on 16/9/5.
 */
public class FileUtils {

    /**
     * 文件或者目录是否存在
     * @param path 路径
     * @return true-存在 false-不存在
     */
    public static boolean isExist(String path) {
        File file = new File(Environment.getExternalStorageDirectory() + path);
        return file.exists();
    }

    /**
     * 获取挂载根目录
     * @return ROOT_DIR
     */
    public static String getRootDir() {
        return Environment.getExternalStorageDirectory() + "";
    }

    /**
     * 判断目录是否存在 不存在则mkdir
     * @param path 路径
     * @return file
     */
    public static File makeDir(String path) {
        File file = new File(Environment.getExternalStorageDirectory() + path);
        if(!file.exists()) {
            file.mkdirs();
        }

        return file;
    }

    /**
     * 复制单个文件
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        }
        catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }

    /**
     * 递归删除文件或子文件夹
     * @param path 路径
     */
    public static void deleteFile(String path) {
        File file = new File(path);
        if(!file.exists()) {
            return;
        }

        if(file.isFile()) {
            file.delete();
            return;
        }

        if(file.isDirectory()) {
            for(File f : file.listFiles()) {
                deleteFile(f.getAbsolutePath());
            }
            file.delete();
        } else{
            file.delete();
        }
    }
}
