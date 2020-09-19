package ru.itmo.model.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Command {
    public final String EXIT = "exit";
    public final String HELP = "help";
    public final String INFO = "info";
    public final String SHOW_ALL_ITEMS = "show";
    public final String REMOVE_ITEM_BY_ID = "remove_by_id";
    public final String CLEAR_COLLECTION = "clear";
    public final String SAVE_ITEM = "save";
    public final String ADD_NEW_ITEM = "add";
    public final String EXECUTE_SCRIPT = "execute_script";
    public final String INSERT_ITEM_AT_INDEX = "insert_at";
    public final String REMOVE_LOWER = "remove_lower";
    public final String ADD_NEW_ITEM_IF_MAX = "add_if_max";
    public final String REMOVE_ALL_ITEMS_BY_DISTANCE = "remove_all_by_distance";
    public final String MAX_ITEM_BY_NAME = "max_by_name";
    public final String GROUP_BY_ID = "group_counting_by_id";
    public final String UPDATE_ITEM_BY_ID = "update";
    public final String GET_ELEMENT_AT_INDEX = "get_at_index";
    public final String ADD_FROM_SCRIPT = "add_from_script";
}
