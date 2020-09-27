package ru.itmo.service.holder;

import ru.itmo.model.Route;

import java.util.List;

public interface RouteHolder {
    void addElement(Route route);

    void addElementIfMax(Route route);

    void insertElementAtIndex(int index, Route route);

    void updateElement(long id, Route route);

    String getElementByMaxName();

    String getInfo();

    boolean checkExistsId(Long id);

    void writeToFile(String fileName);

    String groupCountingById();

    String showAllElements();

    void removeElementById(Long id);

    void removeElementsLessThanLower(Route route);

    void removeElementsIfDistanceAreEqual(long distance);

    void clear();

    void initCollection(List<Route> routeList);
}