package com.yagodar.android.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Yagodar on 31.07.13.
 */
public class Log {
    public static void f(String logTag, String msg) {
        f(logTag, msg, false);
    }

    public static void f(String logTag, String msg, boolean printStackTrace) {
        File file = new File(LOG_FILE_PATH);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            bw.append("[" + new SimpleDateFormat("HH:mm:ss:SSS").format(new Date()) + "]\n[" + logTag + "]\n" + msg + "\n");

            if(printStackTrace) {
                bw.append(getStackTraceStr());
                bw.append("\n");
            }

            bw.append("\n");

            bw.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getStackTraceStr() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        String stackTraceOut = "\tStack trace:\n";
        if(stackTrace != null && stackTrace.length > 0) {
            for (int i = 4; i < stackTrace.length; i++) {
                stackTraceOut += "\tline [" +  stackTrace[i].getLineNumber() + "]\t" + stackTrace[i].getClassName() + "." + stackTrace[i].getMethodName() + " <- \n";
            }

            stackTraceOut = stackTraceOut.substring(0, stackTraceOut.length() - 5);
        }
        else {
            stackTraceOut += "[empty]";
        }

        return stackTraceOut;
    }

    private final static String LOG_FILE_PATH = "sdcard/" + new SimpleDateFormat("yyyy.MM.dd").format(new Date()) + ".log";
}
