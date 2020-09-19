package ru.itmo.service.holder;

import ru.itmo.context.Context;
import ru.itmo.model.State;
import ru.itmo.service.container.CommandContainer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static ru.itmo.model.constant.Command.*;

public class CommandHolderImpl implements CommandHolder {
    private static final CommandContainer COMMAND = Context.getContext().getCommand();

    private final Map<String, Function<String, State>> EXTERNAL = new HashMap<>() {{
        put(HELP, (s) -> COMMAND.help());
        put(EXIT, (s) -> COMMAND.exit());
        put(INFO, (s) -> COMMAND.info());
        put(SHOW_ALL_ITEMS, (s) -> COMMAND.showAllItems());
        put(CLEAR_COLLECTION, (s) -> COMMAND.clear());
        put(SAVE_ITEM, (s) -> COMMAND.save());
        put(MAX_ITEM_BY_NAME, (s) -> COMMAND.maxItemByName());
        put(GROUP_BY_ID, (s) -> COMMAND.groupElementsById());
        put(REMOVE_ITEM_BY_ID, COMMAND::removeById);
        put(ADD_NEW_ITEM, (s) -> COMMAND.insertNewItem());
        put(EXECUTE_SCRIPT, COMMAND::executeScript);
        put(INSERT_ITEM_AT_INDEX, COMMAND::insetItemAtPosition);
        put(REMOVE_LOWER, (s) -> COMMAND.removeLower());
        put(ADD_NEW_ITEM_IF_MAX, (s) -> COMMAND.insertNewItemIfMax());
        put(REMOVE_ALL_ITEMS_BY_DISTANCE, COMMAND::removeItemsByDistance);
        put(UPDATE_ITEM_BY_ID, COMMAND::updateElementById);
        put(GET_ELEMENT_AT_INDEX, COMMAND::getElementAtIndex);
    }};

    @Override
    public Function<String, State> command(String command) {
        if (!EXTERNAL.containsKey(command)) {
            System.out.printf("Wrong command: %s%n", command);
            return (s) -> State.FAILED;
        }
        return EXTERNAL.get(command);
    }
}
