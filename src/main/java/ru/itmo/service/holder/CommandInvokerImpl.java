package ru.itmo.service.holder;

import ru.itmo.context.Context;
import ru.itmo.exception.ValidateException;
import ru.itmo.model.Route;
import ru.itmo.model.constant.Commands;
import ru.itmo.service.command.*;
import ru.itmo.util.ParseUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static ru.itmo.model.constant.Commands.*;
import static ru.itmo.util.LogUtils.log;

public class CommandInvokerImpl implements CommandInvoker {

    private String fileName;

    @Override
    public boolean invoke(Scanner scanner) {
        System.out.println("Enter a command: ");
        try {
            String value = scanner.nextLine();
            log(value);
            List<String> list = ParseUtils.parseStringToTwoValues(value);
            Command command = validateCommand(list, scanner);
            if (command == null) {
                return false;
            }
            command.execute();
            System.out.println("Command completed successfully");
        } catch (Exception exception) {
            System.out.printf("%s%n%s%n", exception.getMessage(), "Command execution aborted");
        }
        return true;
    }

    @Override
    public boolean init(String fileName, Scanner scanner) {
        this.fileName = fileName;
        try {
            List<Route> routeList = new ArrayList<>(Context.getContext().getXmlParser().readDataFromFile(fileName));
            Context.getContext().getRouteHolder().initCollection(routeList);
            return true;
        } catch (Exception exception) {
            System.out.println("Want to continue without a file? Y/N");
            return continueWithoutFile(scanner.nextLine());
        }
    }

    private boolean continueWithoutFile(String answer) {
        return "y".equalsIgnoreCase(answer);
    }

    private Command validateCommand(List<String> commands, Scanner scanner) {
        if (commands.isEmpty()) {
            throw new ValidateException("Command cannot be equal to space or empty");
        }

        if (commands.size() == 1) {
            return getCommand(commands.get(0), scanner, null);
        }
        return getCommand(commands.get(0), scanner, commands.get(1));
    }

    private Command getCommand(String command, Scanner scanner, String parameter) {
        return switch (Commands.findCommand(command)) {
            case HELP -> new InformationCommand(HELP);
            case INFO -> new InformationCommand(INFO);
            case SHOW_ALL_ITEMS -> new InformationCommand(SHOW_ALL_ITEMS);
            case MAX_ITEM_BY_NAME -> new InformationCommand(MAX_ITEM_BY_NAME);
            case GROUP_BY_ID -> new InformationCommand(GROUP_BY_ID);
            case CLEAR_COLLECTION -> new RemoveCommand(CLEAR_COLLECTION);
            case SAVE_COLLECTION_INTO_FILE -> new SaveCommand(this.fileName);
            case REMOVE_ITEM_BY_ID -> new RemoveCommand(REMOVE_ITEM_BY_ID, scanner, parameter);
            case ADD_NEW_ITEM -> new InsertCommand(ADD_NEW_ITEM, scanner, parameter);
            case EXECUTE_SCRIPT -> new ExecuteScriptCommand(parameter);
            case INSERT_ITEM_AT_INDEX -> new InsertCommand(INSERT_ITEM_AT_INDEX, scanner, parameter);
            case REMOVE_LOWER -> new RemoveCommand(REMOVE_LOWER, scanner, null);
            case ADD_NEW_ITEM_IF_MAX -> new InsertCommand(ADD_NEW_ITEM_IF_MAX, scanner, null);
            case REMOVE_ALL_ITEMS_BY_DISTANCE -> new RemoveCommand(REMOVE_ALL_ITEMS_BY_DISTANCE, null, parameter);
            case UPDATE_ITEM_BY_ID -> new InsertCommand(UPDATE_ITEM_BY_ID, scanner, parameter);
            case EXIT -> null;
        };
    }
}