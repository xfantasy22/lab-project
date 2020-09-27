package ru.itmo.service;

import ru.itmo.context.ClientContext;
import ru.itmo.exception.ValidateException;
import ru.itmo.model.ClientRequest;
import ru.itmo.model.Command;
import ru.itmo.util.ParseUtils;

import java.util.List;
import java.util.Scanner;

import static ru.itmo.util.LogUtils.log;

public class CommandInvokerImpl implements CommandInvoker {

    @Override
    public boolean invoke(Scanner scanner) {
        System.out.println("Enter a command: ");
        try {
            String value = scanner.nextLine();
            log(value);
            List<String> list = ParseUtils.parseStringToTwoValues(value);
            Command command = validateCommand(list);
            if (Command.EXIT == command) {
                return false;
            }
            sendData(command, list, scanner);
        } catch (Exception exception) {
            System.out.printf("%s%n", exception.getMessage());
        }
        return true;
    }

    private Command validateCommand(List<String> commands) {
        if (commands.isEmpty()) {
            throw new ValidateException("Command cannot be equal to space or empty");
        }
        return Command.findCommand(commands.get(0));
    }

    private void sendData(Command command, List<String> list, Scanner scanner) {
        Client client = ClientContext.getInstance().getClient();
        if (Command.isWriteCommand(command)) {
            sendDataWithContent(command, list, scanner);
        } else {
            ClientRequest clientRequest = ClientRequest.builder().command(command).build();
            System.out.println(client.sendAndReceiveData(clientRequest));
        }
    }

    private void sendDataWithContent(Command command, List<String> list, Scanner scanner) {
        if (command.isRequireArgument()) {
            validateArgument(list);
            createRequest(scanner, command, list.get(1));
        } else {
            createRequest(scanner, command, null);
        }
    }

    private void validateArgument(List<String> list) {
        if (list.size() != 2) {
            throw new ValidateException("Command required argument");
        }
    }

    private void createRequest(Scanner scanner, Command command, String argument) {
        ClientContext context = ClientContext.getInstance();
        if (Command.EXECUTE_SCRIPT == command) {
            context.getScriptExecutor().execute(argument);
        } else {
            System.out.println(context.getClient().sendAndReceiveData(createQuery(scanner, command, argument)));
        }
    }

    private ClientRequest createQuery(Scanner scanner, Command command, String argument) {
        RouteCreator creator = ClientContext.getInstance().getRouteCreator();
        return ClientRequest.builder()
                .route(creator.createRoute(scanner, Command.UPDATE_ITEM_BY_ID == command))
                .argument(ParseUtils.parseArgument(argument))
                .command(command)
                .build();
    }
}