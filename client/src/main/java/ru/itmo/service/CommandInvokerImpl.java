package ru.itmo.service;

import ru.itmo.context.ClientContext;
import ru.itmo.exception.ValidateException;
import ru.itmo.model.ClientRequest;
import ru.itmo.model.Command;
import ru.itmo.model.domain.User;
import ru.itmo.model.dto.RouteView;
import ru.itmo.util.ParseUtils;

import java.util.List;
import java.util.Scanner;

import static ru.itmo.util.LogUtils.log;

public class CommandInvokerImpl implements CommandInvoker {

    private User user;

    @Override
    public boolean invoke(Scanner scanner) {
        if (user == null) {
            try {
                return initUser(scanner);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println("Try again? Y/N");
                if ("y".equalsIgnoreCase(scanner.nextLine())) {
                    user = null;
                    return true;
                }
                return false;
            }
        }
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
            ClientRequest clientRequest = ClientRequest.builder()
                    .user(user)
                    .command(command).build();
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

    public boolean initUser(Scanner scanner) {
        System.out.println("Already have an account? Y/N");
        if (createOrLoginUser(scanner, true)) {
            return true;
        }

        System.out.println("Want to create an account? Y/N");
        return createOrLoginUser(scanner, false);
    }

    public boolean createOrLoginUser(Scanner scanner, boolean isLogin) {
        if ("y".equalsIgnoreCase(scanner.nextLine())) {
            User.UserBuilder builder = User.builder();
            System.out.println("Enter a username");
            builder.username(scanner.nextLine());
            System.out.println("Enter a password");
            builder.password(scanner.nextLine());
            user = builder.build();
            ClientRequest request = ClientRequest.builder()
                    .user(user)
                    .command(isLogin ? Command.SIGN_IN : Command.SIGN_UP)
                    .build();
            System.out.println(ClientContext.getInstance().getClient().sendAndReceiveData(request));
            return true;
        }
        return false;
    }

    private ClientRequest createQuery(Scanner scanner, Command command, String argument) {
        RouteCreator creator = ClientContext.getInstance().getRouteCreator();
        RouteView route = Command.requireRoute(command) ? creator.createRoute(scanner, Command.UPDATE_ITEM_BY_ID == command) : null;
        return ClientRequest.builder()
                .route(route)
                .user(user)
                .argument(ParseUtils.parseArgument(argument))
                .command(command)
                .build();
    }
}