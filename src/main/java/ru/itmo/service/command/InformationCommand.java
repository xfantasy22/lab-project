package ru.itmo.service.command;

import lombok.AllArgsConstructor;
import ru.itmo.model.constant.Commands;

@AllArgsConstructor
public class InformationCommand extends AbstractCommand {

    private final Commands constant;

    @Override
    public void execute() {
        switch (constant) {
            case HELP -> help();
            case INFO -> getRouteHolder().getInfo();
            case SHOW_ALL_ITEMS -> getRouteHolder().showAllElements();
            case GROUP_BY_ID -> getRouteHolder().groupCountingById();
            case MAX_ITEM_BY_NAME -> getRouteHolder().getElementByMaxName();
        }
    }

    private void help() {
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
    }
}
