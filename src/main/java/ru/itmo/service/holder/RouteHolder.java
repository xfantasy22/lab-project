package ru.itmo.service.holder;

import ru.itmo.model.Route;

import java.util.List;

public interface RouteHolder {
    Route addElement(Route route);

    void addElementIfMax(Route route);

    void insertElementAtIndex(int index, Route route);

    void updateElement(Long id, Route route);

    void getElementByMaxName();

    void getInfo();

    boolean checkExistsId(Long id);

    void groupCountingById();

    void showAllElements();

    void removeElementById(Long id);

    void removeElementsLessThanLower(Route route);

    void removeElementsIfDistanceAreEqual(long distance);

    void clear();

    void writeToFile(String fileName);

    void initCollection(List<Route> routeList);

    Long getNextId();
}