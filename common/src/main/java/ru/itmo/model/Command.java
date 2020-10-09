package ru.itmo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.itmo.exception.ValidateException;

import java.util.Arrays;
import java.util.function.Predicate;

@Getter
@AllArgsConstructor
public enum Command {
    EXIT("exit", false),
    HELP("help", false),
    INFO("info", false),
    SHOW_ALL_ITEMS("show", false),
    REMOVE_ITEM_BY_ID("remove_by_id", true),
    CLEAR_COLLECTION("clear", false),
    SAVE_COLLECTION_INTO_FILE("save", false),
    ADD_NEW_ITEM("add", false),
    EXECUTE_SCRIPT("execute_script", true),
    INSERT_ITEM_AT_INDEX("insert_at", true),
    REMOVE_LOWER("remove_lower", false),
    ADD_NEW_ITEM_IF_MAX("add_if_max", false),
    REMOVE_ALL_ITEMS_BY_DISTANCE("remove_all_by_distance", true),
    MAX_ITEM_BY_NAME("max_by_name", false),
    GROUP_BY_ID("group_counting_by_id", false),
    UPDATE_ITEM_BY_ID("update", true),
    SIGN_IN("sign_in", false),
    SIGN_UP("sign_up", false);

    private final String command;
    private final boolean requireArgument;

    public static Command findCommand(String command) {
        return Arrays.stream(values())
                .filter(value -> value.getCommand().equalsIgnoreCase(command))
                .findFirst()
                .orElseThrow(() -> new ValidateException(String.format("Wrong command: %s", command)));
    }

    public static boolean isWriteCommand(Command command) {
        return Arrays.stream(values())
                .filter(isWrite().or(Command::isRequireArgument))
                .anyMatch(value -> value == command);
    }

    private static Predicate<Command> isWrite() {
        return value -> value == ADD_NEW_ITEM_IF_MAX || value == ADD_NEW_ITEM || value == REMOVE_LOWER;
    }

    public static boolean requireRoute(Command command) {
        return REMOVE_ALL_ITEMS_BY_DISTANCE != command && REMOVE_ITEM_BY_ID != command;
    }
}
