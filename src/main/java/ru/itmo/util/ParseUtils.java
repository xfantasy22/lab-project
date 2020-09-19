package ru.itmo.util;

import ru.itmo.exception.ValidateException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ParseUtils {
    public static List<String> parseStringToTwoValues(String item) {
        return parseStringToValues(item, 2);
    }

    public static List<String> parseStringToValues(String item, int limit) {
        List<String> list = Arrays.stream(item.split(" "))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .collect(Collectors.toList());
        if (list.size() > limit) {
            throw new ValidateException("Expected count values: " + limit + ", but actual count: " + list.size());
        }
        return list;
    }
}
