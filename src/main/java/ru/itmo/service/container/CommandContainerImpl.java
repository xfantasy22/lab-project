package ru.itmo.service.container;

import ru.itmo.context.Context;
import ru.itmo.model.State;
import ru.itmo.service.holder.RouteHolder;
import ru.itmo.util.validator.FileValidator;

import java.io.File;


public class CommandContainerImpl implements CommandContainer {
    private static final RouteHolder ROUTE_HOLDER = Context.getContext().getRouteHolder();

    @Override
    public State help() {
        System.out.println("help : вывести справку по доступным командам\n" +
                "info : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)\n" +
                "show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении\n" +
                "add {element} : добавить новый элемент в коллекцию\n" +
                "update {element} : обновить значение элемента коллекции, id которого равен заданному\n" +
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
                "group_counting_by_id : сгруппировать элементы коллекции по значению поля id, вывести количество элементов в каждой группе");
        return State.RUN;
    }

    @Override
    public State exit() {
        return State.STOP;
    }

    @Override
    public State info() {
        System.out.println(ROUTE_HOLDER.getInfo());
        return State.RUN;
    }

    @Override
    public State showAllItems() {
        ROUTE_HOLDER.showAllElements();
        return State.RUN;
    }

    @Override
    public State clear() {
        ROUTE_HOLDER.clear();
        return State.RUN;
    }

    @Override
    public State save() {
        ROUTE_HOLDER.writeToFile();
        return State.RUN;
    }

    @Override
    public State maxItemByName() {
        System.out.println(ROUTE_HOLDER.getElementByMaxName());
        return State.RUN;
    }

    @Override
    public State groupElementsById() {
        System.out.println(ROUTE_HOLDER.groupCountingById());
        return State.RUN;
    }

    @Override
    public State removeById(String id) {
        Long safeId = Long.parseLong(id);
        if (ROUTE_HOLDER.checkExistsId(Long.parseLong(id))) {
            ROUTE_HOLDER.removeElementById(safeId);
            return State.RUN;
        }
        System.out.printf("Id does not exists, id: %s%n", id);
        return State.FAILED;
    }

    @Override
    public State insertNewItem() {
        ROUTE_HOLDER.addElement(Context.getContext().getRouteParser().addRoute(ROUTE_HOLDER.getNextId()));
        return State.RUN;
    }

    @Override
    public State executeScript(String scriptFileName) {
        try {
            File scriptFile = new File(scriptFileName);
            FileValidator.checkReadProperty(scriptFile);
            return Context.getContext().getScriptExecutor().executeScript(scriptFile);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return State.FAILED;
        }
    }

    @Override
    public State insetItemAtPosition(String item) {
        ROUTE_HOLDER.insertElementAtIndex(Integer.parseInt(item), Context.getContext().getRouteParser().addRoute(ROUTE_HOLDER.getNextId()));
        return State.RUN;
    }

    @Override
    public State removeLower() {
        ROUTE_HOLDER.removeElementsLessThanLower(Context.getContext().getRouteParser().addRoute(ROUTE_HOLDER.getNextId()));
        return State.RUN;
    }

    @Override
    public State insertNewItemIfMax() {
        ROUTE_HOLDER.addElementIfMax(Context.getContext().getRouteParser().addRoute(ROUTE_HOLDER.getNextId()));
        return State.RUN;
    }

    @Override
    public State removeItemsByDistance(String distance) {
        ROUTE_HOLDER.removeElementsIfDistanceAreEqual(Long.parseLong(distance));
        return State.RUN;
    }

    @Override
    public State updateElementById(String data) {
        Long id = Long.parseLong(data);
        if (ROUTE_HOLDER.checkExistsId(id)) {
            ROUTE_HOLDER.updateElement(id, Context.getContext().getRouteParser().updateRoute());
            return State.RUN;
        }
        System.out.printf("Element with id: %s is not present%n", id);
        return State.FAILED;
    }

    @Override
    public State getElementAtIndex(String index) {
        ROUTE_HOLDER.getElementAtIndex(Integer.parseInt(index));
        return State.RUN;
    }
}
