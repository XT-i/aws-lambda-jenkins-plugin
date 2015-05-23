package com.xti.jenkins.plugin.awslambda.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class LogUtils {
    public static String getStackTrace(Throwable t){
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        String stackTrace = sw.toString();
        pw.close();
        try{
            sw.close();
        }catch(Exception ignored){
        }
        return stackTrace;
    }
}
