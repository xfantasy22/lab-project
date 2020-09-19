package ru.itmo.service.holder;

import ru.itmo.model.Route;
import ru.itmo.model.State;

import java.util.List;
import java.util.Map;

public interface RouteHolder {
    Route addElement(Route route);

    void addElementIfMax(Route route);

    void insertElementAtIndex(int index, Route route);

    void updateElement(Long id, Route route);

    void getElementAtIndex(int index);

    Route getElementByMaxName();

    String getInfo();

    boolean checkExistsId(Long id);

    Map<Long, List<Route>> groupCountingById();

    void showAllElements();

    void removeElementById(Long id);

    void removeElementsLessThanLower(Route route);

    void removeElementsIfDistanceAreEqual(long distance);

    void clear();

    void writeToFile();

    State readFromFile(String fileName);

    Long getNextId();
}