package ru.itmo.service.command;

import lombok.extern.slf4j.Slf4j;
import ru.itmo.context.ServerContext;
import ru.itmo.model.ClientRequest;
import ru.itmo.model.Command;
import ru.itmo.model.ServerResponse;
import ru.itmo.model.Status;
import ru.itmo.service.holder.RouteHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static ru.itmo.model.Command.*;

@Slf4j
public class ReadCommand {
    private static final RouteHolder ROUTE_HOLDER = ServerContext.getInstance().getRouteHolder();

    private final Map<Command, Supplier<String>> READ_COMMAND = new HashMap<Command, Supplier<String>>() {{
        put(HELP, getHelp());
        put(INFO, ROUTE_HOLDER::getInfo);
        put(SHOW_ALL_ITEMS, ROUTE_HOLDER::showAllElements);
        put(MAX_ITEM_BY_NAME, ROUTE_HOLDER::getElementByMaxName);
        put(GROUP_BY_ID, ROUTE_HOLDER::groupCountingById);
        put(CLEAR_COLLECTION, getClear());
    }};

    public ServerResponse getResult(ClientRequest request) {
        try {
            return ServerResponse.builder()
                    .status(Status.Success)
                    .response(READ_COMMAND.get(request.getCommand()).get())
                    .build();
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return ServerResponse.builder()
                    .status(Status.Failed)
                    .error(exception.getMessage())
                    .build();
        }
    }

    private Supplier<String> getClear() {
        return () -> {
            ROUTE_HOLDER.clear();
            return "success";
        };
    }

    private Supplier<String> getHelp() {
        return () -> "help : вывести справку по доступным командам\n" +
                "info : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)\n" +
                "show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении\n" +
                "add {element} : добавить новый элемент в коллекцию\n" +
                "update id {element} : обновить значение элемента коллекции, id которого равен заданному\n" +
                "remove_by_id id : удалить элемент из коллекции по его id\n" +
                "clear : очистить коллекцию\n" +
                "save : сохранить коллекцию в файл\n" +
                "execute_script file_name : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.\n" +
                "exit : завершить программу (без сохранения в файл)\n" +
                "insert_at index {element} : добавить новый элемент в заданную позицию\n" +
                "add_if_max {element} : добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции\n" +
                "remove_lower {element} : удалить из коллекции все элементы, меньшие, чем заданный\n" +
                "remove_all_by_distance distance : удалить из коллекции все элементы, значение поля distance которого эквивалентно заданному\n" +
                "max_by_name : вывести любой объект из коллекции, значение поля name которого является максимальным\n" +
                "group_counting_by_id : сгруппировать элементы коллекции по значению поля id, вывести количество элементов в каждой группе";
    }
}