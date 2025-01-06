package org.example.control.telegram.pages.helpers;

public enum PageName {
    START("/start", false),
    USER_MENU("Меню", false),
    CREATE_TASK("Новая задача", false),
    SHOW_INFO("Информация", false),
    SHOW_MY_TASKS("Мои задачи", false),
    CHANGE_BIO("CHANGE-BIO", false),
    SELECT_TASK("TASK-TO-SHOW", false),
    SET_BIO_WAITING("SETBIO-WAITING", true),
    SHOW_MY_DRAFTS("Черновики", false),
    SHOW_FINISHED_TASKS("История", false),
    SET_TASK_TITLE_WAITING("SET-TASK-TITLE-WAITING", true),
    SET_TITLE_WAITING("SETTITLE-WAITING", true),
    SET_DESCRIPTION("SETDESCR", false),
    SET_DEADLINE("SETDEADLINE", false),
    SET_REW("SETREW", false),
    SET_TITLE("SETTITLE", false),
    PUBLIC("PUBLIC", false),
    UNPUBLIC("UNPUBLIC", false),
    FINISH("FINISH", false),
    EDIT("EDIT", false),
    EXIT("EXIT", false),
    SET_DESCRIPTION_WAITING("SETDESCR-WAITING", true),
    SET_REW_WAITING("SETREW-WAITING", true),
    SET_DEADLINE_WAITING("SETDEADLINE-WAITING", true);

    private final String command;
    private final boolean requiresArgs;

    PageName(String command, boolean requiresArgs) {
        this.command = command;
        this.requiresArgs = requiresArgs;
    }

    public String getCommand() {
        return command;
    }

    public boolean requiresArgs() {
        return requiresArgs;
    }

    public static PageName fromString(String command) {
        for (PageName type : PageName.values()) {
            if (type.getCommand().equals(command)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown command: " + command);
    }

    public static boolean requiresArgs(String command) {
        try {
            return fromString(command).requiresArgs();
        } catch (IllegalArgumentException e) {
            return false; // Или обработайте случай с неизвестной командой по-другому
        }
    }
}
