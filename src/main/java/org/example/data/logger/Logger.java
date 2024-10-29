package org.example.data.logger;

import org.example.ui.Console;

public class Logger {
    private String textColor;
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_CYAN = "\u001B[36m";

    private String output;
    private boolean debug = false;
    private LogType type;
    private String massage;
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
    public void setLog(String massage, LogType type){
        this.massage = massage;
        this.type = type;
    }
    public void print() {
        switch (type) {
            case INFO:
                textColor = ANSI_GREEN;
                break;
            case WARN:
                textColor = ANSI_YELLOW;
                break;
            case ERROR:
                textColor = ANSI_RED;
                break;
            case DEBUG_FATAL:
                textColor = ANSI_CYAN;
                break;
            case TRACE:
                textColor = ANSI_RESET;
                break;
        }
        if ( debug == false && type == LogType.DEBUG_FATAL) output = "";
        else output = textColor + "[ " + type + " ] : " + massage + "\n";
        System.out.printf(output);
    }
}
