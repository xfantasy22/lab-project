package ru.itmo.util;

import lombok.SneakyThrows;
import ru.itmo.exception.ValidateException;

import java.text.NumberFormat;
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
            throw new ValidateException("Data is not valid, data: " + item);
        }
        return list;
    }

    @SneakyThrows
    public static Number parseArgument(String argument) {
        if (argument == null) {
            return null;
        }
        boolean isNumber = argument.chars().allMatch(Character::isDigit);
        if (isNumber) {
            return NumberFormat.getInstance().parse(argument);
        }
        throw new ValidateException("Argument value is not valid, argument " + argument);
    }
}
