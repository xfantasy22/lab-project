package ru.itmo.util;

public class LogUtils {
    private static boolean fullLogs;

    public static void log(String value) {
        if (fullLogs) {
            System.out.println(value);
        }
    }

    public static void setFullLogs(boolean fullLogs) {
        LogUtils.fullLogs = fullLogs;
    }
}
