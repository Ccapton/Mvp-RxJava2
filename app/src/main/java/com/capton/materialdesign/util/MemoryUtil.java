package com.capton.materialdesign.util;

import android.app.ActivityManager;
import android.content.Context;
import android.text.format.Formatter;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by capton on 2017/8/31.
 */

public class MemoryUtil {
    static Context sContext;
    static MemoryUtil sMemoryUti;
    public static MemoryUtil getInstance(Context context){
        if(sMemoryUti==null)
            sMemoryUti=new MemoryUtil();
        sContext=context;
        return sMemoryUti;
    }

    public  String getAvailMemory( ) {// 获取android当前可用内存大小

        ActivityManager am = (ActivityManager) sContext.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        //mi.availMem; 当前系统的可用内存

        return Formatter.formatFileSize(sContext, mi.availMem);// 将获取的内存大小规格化
    }

    public  long getAvailMemoryNumber( ) {// 获取android当前可用内存大小

        ActivityManager am = (ActivityManager) sContext.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        //mi.availMem; 当前系统的可用内存

        return mi.availMem/1024;
    }
    public  String getTotalMemory() {
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;

        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
                    localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小
            str2=str2.trim();
            String numberStrng="";
            if(str2 != null && !"".equals(str2)) {
                for (int i = 0; i < str2.length(); i++) {
                    if (str2.charAt(i) >= 48 && str2.charAt(i) <= 57) {
                        numberStrng += str2.charAt(i);
                    }
                }
            }
            initial_memory = Integer.valueOf(numberStrng).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
            localBufferedReader.close();

        } catch (IOException e) {
        }
        return Formatter.formatFileSize(sContext, initial_memory);// Byte转换为KB或者MB，内存大小规格化
    }
    public long getTotalMemoryNumber(){
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;

        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
                    localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小
            str2=str2.trim();
            String numberStrng="";
            if(str2 != null && !"".equals(str2)) {
                for (int i = 0; i < str2.length(); i++) {
                    if (str2.charAt(i) >= 48 && str2.charAt(i) <= 57) {
                        numberStrng += str2.charAt(i);
                    }
                }
            }
            initial_memory = Integer.valueOf(numberStrng).intValue() ;//
            localBufferedReader.close();

        } catch (IOException e) {
        }
        return initial_memory;
    }
}
