package ru.itmo.service.parser.xml;

import ru.itmo.model.Route;

import java.util.List;

public interface XmlParser {

    List<Route> readDataFromFile(String fileName);

    void writeDataToFile(List<Route> routes, String fileName);

}
