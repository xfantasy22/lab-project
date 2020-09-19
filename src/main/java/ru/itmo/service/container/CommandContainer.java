package ru.itmo.service.container;

import ru.itmo.model.State;

public interface CommandContainer {
    State help();

    State exit();

    State info();

    State showAllItems();

    State clear();

    State save();

    State maxItemByName();

    State groupElementsById();

    State removeById(String id);

    State insertNewItem();

    State executeScript(String scriptFileName);

    State insetItemAtPosition(String item);

    State removeLower();

    State insertNewItemIfMax();

    State removeItemsByDistance(String distance);

    State updateElementById(String data);

    State getElementAtIndex(String index);
}
