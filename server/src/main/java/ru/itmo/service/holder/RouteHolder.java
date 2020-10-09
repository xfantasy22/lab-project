package ru.itmo.service.holder;

import ru.itmo.model.ClientRequest;
import ru.itmo.model.domain.User;

public interface RouteHolder {
    void addElement(ClientRequest request);

    void addElementIfMax(ClientRequest request);

    void insertElementAtIndex(ClientRequest request);

    void updateElement(ClientRequest request);

    String getElementByMaxName();

    String getInfo();

    boolean checkExistsId(Long id);

    String groupCountingById();

    String showAllElements();

    void removeElementById(ClientRequest request);

    void removeElementsLessThanLower(ClientRequest request);

    void removeElementsIfDistanceAreEqual(ClientRequest request);

    void clear(User user);

    void initCollection();
}