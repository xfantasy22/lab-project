package ru.itmo.model.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.itmo.exception.ValidateException;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum Commands {
    EXIT("exit"),
    HELP("help"),
    INFO("info"),
    SHOW_ALL_ITEMS("show"),
    REMOVE_ITEM_BY_ID("remove_by_id"),
    CLEAR_COLLECTION("clear"),
    SAVE_COLLECTION_INTO_FILE("save"),
    ADD_NEW_ITEM("add"),
    EXECUTE_SCRIPT("execute_script"),
    INSERT_ITEM_AT_INDEX("insert_at"),
    REMOVE_LOWER("remove_lower"),
    ADD_NEW_ITEM_IF_MAX("add_if_max"),
    REMOVE_ALL_ITEMS_BY_DISTANCE("remove_all_by_distance"),
    MAX_ITEM_BY_NAME("max_by_name"),
    GROUP_BY_ID("group_counting_by_id"),
    UPDATE_ITEM_BY_ID("update");

    private final String value;

    public static Commands findCommand(String command) {
        return Arrays.stream(values())
                .filter(value -> value.getValue().equalsIgnoreCase(command))
                .findFirst()
                .orElseThrow(() -> new ValidateException("Wrong command: " + command));
    }
}
