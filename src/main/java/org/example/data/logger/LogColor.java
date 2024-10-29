package org.example.data.logger;

public enum LogColor {
    ANSI_RESET("\u001B[0m"),
    ANSI_RED("\u001B[31m"),
    ANSI_GREEN("\u001B[32m"),
    ANSI_YELLOW("\u001B[33m"),
    ANSI_CYAN("\u001B[36m");

    private final String color;
    LogColor(String color) {
        this.color = color;
    }
    public String getColor() {
        return color;
    }
}
