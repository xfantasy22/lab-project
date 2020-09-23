package ru.itmo.model.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.itmo.exception.ValidateException;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum Commands {
    EXIT("exit", 0),
    HELP("help", 0),
    INFO("info", 0),
    SHOW_ALL_ITEMS("show", 0),
    REMOVE_ITEM_BY_ID("remove_by_id", 1),
    CLEAR_COLLECTION("clear", 0),
    SAVE_COLLECTION_INTO_FILE("save", 0),
    ADD_NEW_ITEM("add", 0),
    EXECUTE_SCRIPT("execute_script", 1),
    INSERT_ITEM_AT_INDEX("insert_at", 1),
    REMOVE_LOWER("remove_lower", 0),
    ADD_NEW_ITEM_IF_MAX("add_if_max", 0),
    REMOVE_ALL_ITEMS_BY_DISTANCE("remove_all_by_distance", 1),
    MAX_ITEM_BY_NAME("max_by_name", 0),
    GROUP_BY_ID("group_counting_by_id", 0),
    UPDATE_ITEM_BY_ID("update", 1);

    private final String value;
    private final int arg;

    public static Commands findCommand(String command, String parameter) {
        int agrFromConsole;
        if (parameter == null) {
            agrFromConsole = 0;
        } else {
            agrFromConsole = 1;
        }
        return Arrays.stream(values())
                .filter(value -> value.getValue().equalsIgnoreCase(command))
                .filter(value -> value.getArg() == agrFromConsole)
                .findFirst()
                .orElseThrow(
                        () -> new ValidateException(String.format("Wrong command: %s %s", command, getEffectiveParameter(parameter))));
    }

    private static String getEffectiveParameter(String parameter) {
        return parameter == null ? "" : parameter;
    }
}
