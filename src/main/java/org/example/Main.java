package org.example;
import org.example.ui.Console;
import org.example.ui.Telegram;

public class Main {
    public static void main(String[] args) {

        Runnable AdminConsole = new Console();
        //AdminConsole.run();
        Telegram tg = new Telegram();
        tg.run();
    }
}