package org.example.managers;

public enum CommandsNames {
    ADD_USER("add_user"),
    SHOW_USERS("show_users"),
    START("start"),
    START_CONS("start_cons"),
    EXIT("exit")
    ;

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
