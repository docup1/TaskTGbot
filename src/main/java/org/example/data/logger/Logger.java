package org.example.data.logger;

import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Logger{

    private static final Map<LogType, String> colorMap = new EnumMap<>(LogType.class);
    private static boolean DEBUG = false;
    private static boolean DAILY_LOGS = false; // Новый конфигурационный параметр для ежедневных логов
    private static String LOG_FILE_NAME = "logs";
    private static String LOG_FILE_PATH = "output/";
    private static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static String DAILY_LOGS_DATE_FORMAT = "yyyy-MM-dd";

    static {
        colorMap.put(LogType.INFO, LogColor.ANSI_GREEN.getColor());
        colorMap.put(LogType.WARN, LogColor.ANSI_YELLOW.getColor());
        colorMap.put(LogType.ERROR, LogColor.ANSI_RED.getColor());
        colorMap.put(LogType.DEBUG, LogColor.ANSI_CYAN.getColor());
        colorMap.put(LogType.TRACE, LogColor.ANSI_RESET.getColor());
    }
    private static DateTimeFormatter LOG_DATE_FORMATTER = DateTimeFormatter.ofPattern(DAILY_LOGS_DATE_FORMAT);
    private static String formatLogMessage(String message, LogType type) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT).withZone(ZoneId.systemDefault());
        String formattedDate = formatter.format(Instant.now());
        String color = colorMap.getOrDefault(type, LogColor.ANSI_RESET.getColor());
        return color + "[ " + formattedDate + " ] [ " + type + " ] : " + message + LogColor.ANSI_RESET.getColor() + System.lineSeparator();
    }

    public static void init(String confPath) {
        Properties props = new Properties();

        try (FileInputStream fis = new FileInputStream(new File(confPath))) {
            props.load(fis);
        } catch (FileNotFoundException e) {
            System.err.println("Файл конфигурации не найден, будут использованы значения по умолчанию.");
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке конфигурации: " + e.getMessage());
        }
        LOG_FILE_NAME = props.getProperty("LOG_FILE_NAME", LOG_FILE_NAME);
        LOG_FILE_PATH = props.getProperty("LOG_FILE_PATH", LOG_FILE_PATH);
        DATE_FORMAT = props.getProperty("DATE_FORMAT", DATE_FORMAT);
        DEBUG = Boolean.parseBoolean(props.getProperty("DEBUG", String.valueOf(DEBUG)));
        DAILY_LOGS = Boolean.parseBoolean(props.getProperty("DAILY_LOGS", String.valueOf(DAILY_LOGS)));
        DAILY_LOGS_DATE_FORMAT = props.getProperty("DAILY_LOGS_DATE_FORMAT", DAILY_LOGS_DATE_FORMAT);

        // Обновляем форматтер даты для ежедневных логов
        LOG_DATE_FORMATTER = DateTimeFormatter.ofPattern(DAILY_LOGS_DATE_FORMAT);
    }

    private static String getLogFileName() {
        if (DAILY_LOGS) {
            String currentDate = LOG_DATE_FORMATTER.format(LocalDateTime.now());
            return LOG_FILE_NAME + " | " + currentDate;
        }
        return LOG_FILE_NAME;
    }

    public static void put(String message, LogType type) {
        put(message, type, DEBUG); // Вызывает метод с debug = false по умолчанию
    }

    public static void put(String message, LogType type, boolean debug) {
        String output = formatLogMessage(message, type);

        // Запись в файл
        try (FileWriter fileWriter = new FileWriter(LOG_FILE_PATH + getLogFileName(), true);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            if(type == LogType.DEBUG && DEBUG == false) printWriter.print("");
            else printWriter.print(output);
        } catch (IOException e) {
            System.err.println("Ошибка при записи лога в файл: " + e.getMessage());
            System.exit(1);
        }

        // Вывод в консоль, если debug = true
        if (debug) {
            System.out.print(output);
        }
    }

    public static void printLogs() {
        String logFileName = LOG_FILE_PATH + getLogFileName(); // Получаем имя файла с учетом DAILY_LOGS

        System.out.println("Все логи из файла: " + logFileName);
        try (BufferedReader reader = new BufferedReader(new FileReader(logFileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Файл логов не найден: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Ошибка при чтении логов из файла: " + e.getMessage());
        }
    }

    public static void clear() {
        String logFileName = LOG_FILE_PATH + getLogFileName(); // Получаем имя файла с учетом DAILY_LOGS

        try (FileWriter fileWriter = new FileWriter(logFileName, false)) {
            // Пустое тело: открытие файла в этом режиме очищает его
        } catch (IOException e) {
            System.err.println("Ошибка при очистке файла логов: " + e.getMessage());
        }
    }
    public static List<String> searchByType(LogType type) {
        List<String> logsByType = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(LOG_FILE_PATH + getLogFileName()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("[ " + type + " ]")) {
                    logsByType.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении логов из файла: " + e.getMessage());
        }
        return logsByType;
    }

    public static List<String> searchByTime(LocalDateTime logDate) {
        // Определяем начало и конец дня для указанной даты
        LocalDateTime startTime = logDate.toLocalDate().atStartOfDay();
        LocalDateTime endTime = logDate.toLocalDate().atTime(23, 59, 59);

        // Вызываем метод с временным диапазоном на день
        return searchByTime(logDate, startTime, endTime);
    }

    public static List<String> searchByTime(LocalDateTime logDate, LocalDateTime startTime, LocalDateTime endTime) {
        List<String> logsByTime = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT).withZone(ZoneId.systemDefault());

        String logFileName = DAILY_LOGS
                ? LOG_FILE_NAME + " | " + LOG_DATE_FORMATTER.format(logDate)
                : LOG_FILE_NAME;

        try (BufferedReader reader = new BufferedReader(new FileReader(LOG_FILE_PATH + logFileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    int dateStartIndex = line.indexOf("[ ") + 2;
                    int dateEndIndex = line.indexOf(" ]", dateStartIndex);
                    String dateString = line.substring(dateStartIndex, dateEndIndex);
                    LocalDateTime logEntryDateTime = LocalDateTime.parse(dateString, formatter);

                    // Проверка на соответствие дате и временному диапазону
                    if (logEntryDateTime.toLocalDate().isEqual(logDate.toLocalDate()) &&
                            (logEntryDateTime.toLocalTime().isAfter(startTime.toLocalTime()) ||
                                    logEntryDateTime.toLocalTime().equals(startTime.toLocalTime())) &&
                            (logEntryDateTime.toLocalTime().isBefore(endTime.toLocalTime()) ||
                                    logEntryDateTime.toLocalTime().equals(endTime.toLocalTime()))) {
                        logsByTime.add(line);
                    }
                } catch (DateTimeParseException | IndexOutOfBoundsException e) {
                    System.err.println("Ошибка при разборе даты в логах: " + e.getMessage());
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Файл логов для указанной даты не найден: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Ошибка при чтении логов из файла: " + e.getMessage());
        }
        return logsByTime;
    }
}
