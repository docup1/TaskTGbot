package org.example.data.logger;

public class Logger {
    private static String textColor;
    private static String output;
    private static boolean debug = false;
    private static boolean trace = true;
    public void setDebug(boolean trace) {
        this.debug = trace;
    }
    public void setTrace(boolean debug) {
        this.debug = debug;
    }
    public static void print(String massage, LogType type) {
        output = "";
        switch (type) {
            case INFO:
                textColor = LogColor.ANSI_GREEN.getColor();
                break;
            case WARN:
                textColor = LogColor.ANSI_YELLOW.getColor();
                break;
            case ERROR:
                textColor = LogColor.ANSI_RED.getColor();
                break;
            case DEBUG_FATAL:
                textColor = LogColor.ANSI_CYAN.getColor();
                break;
            case TRACE:
                textColor = LogColor.ANSI_RESET.getColor();
                break;
        }
        output += textColor;
        output += "[ " + type + " ] : " + massage + "\n";

        if ( debug == false && type == LogType.DEBUG_FATAL) output = "";
        if ( trace == false && type == LogType.TRACE) output = "";

        System.out.printf(output);
    }
}
