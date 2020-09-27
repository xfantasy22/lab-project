package ru.itmo.service.command;

import lombok.extern.slf4j.Slf4j;
import ru.itmo.context.ServerContext;
import ru.itmo.model.*;
import ru.itmo.service.holder.RouteHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static ru.itmo.model.Command.*;

@Slf4j
public class WriteCommand {
    private static final RouteHolder ROUTE_HOLDER = ServerContext.getInstance().getRouteHolder();

    private final Map<Command, BiConsumer<Route, Number>> WRITE_COMMAND = new HashMap<>() {{
        put(ADD_NEW_ITEM, (r, n) -> ROUTE_HOLDER.addElement(r));
        put(INSERT_ITEM_AT_INDEX, (r, n) -> ROUTE_HOLDER.insertElementAtIndex(n.intValue(), r));
        put(UPDATE_ITEM_BY_ID, (r, n) -> ROUTE_HOLDER.updateElement(n.longValue(), r));
        put(REMOVE_ALL_ITEMS_BY_DISTANCE, (r, n) -> ROUTE_HOLDER.removeElementsIfDistanceAreEqual(n.longValue()));
        put(CLEAR_COLLECTION, (r, n) -> ROUTE_HOLDER.clear());
        put(REMOVE_LOWER, (r, n) -> ROUTE_HOLDER.removeElementsLessThanLower(r));
        put(ADD_NEW_ITEM_IF_MAX, (r, n) -> ROUTE_HOLDER.addElementIfMax(r));
        put(REMOVE_ITEM_BY_ID, (r, n) -> ROUTE_HOLDER.removeElementById(n.longValue()));
    }};

    public ServerResponse getResult(ClientRequest request) {
        try {
            WRITE_COMMAND.get(request.getCommand()).accept(request.getRoute(), request.getArgument());
            return ServerResponse.builder()
                    .response("Success")
                    .status(Status.Success)
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ServerResponse.builder().error(e.getMessage()).status(Status.Failed).build();
        }
    }
}
