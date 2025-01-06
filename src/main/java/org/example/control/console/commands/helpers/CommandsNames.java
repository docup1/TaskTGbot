package org.example.control.console.commands.helpers;

public enum CommandsNames {
    ADD_USER("add_user"),
    UPDATE_BIO("update_bio"),
    CREATE_TASK("create_task"),
    SET_DEADLINE("set_deadline"),
    SET_DESCRIPTION("set_description"),
    SET_MSG_ID("set_msg_id"),
    SET_REWARD("set_reward"),
    SET_TITLE("set_title"),
    SET_STATUS("set_status"),
    USER_INF("user_inf"),
    START("start"),
    START_CONS("start_cons"),
    EXIT("exit"),
    PRINT_LOGS("print_logs"),
    SEARCH_LOGS("search_logs"),
    CLEAR_LOGS("clear_logs"),
    GET_USER("get_user"),
    CONNECT("connect"),
    MY_TASKS("my_tasks"),
    GET_TASK("get_task"),
    GET_MSG_ID("get_msg_id"),
    TASK_COUNT("task_count"),
    GET_TASK_BY_ID("get_task_by_id"),
    GET_TASK_DETAILS_BY_ID("get_task_details_by_id"),;

    private final String command;

    CommandsNames(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public static CommandsNames fromString(String command) {
        for (CommandsNames commandsName : CommandsNames.values()) {
            if (commandsName.command.equalsIgnoreCase(command)) {
                return commandsName;
            }
        }
        throw new IllegalArgumentException("No enum constant for command: " + command);
    }
}
