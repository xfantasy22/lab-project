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
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import static ru.itmo.model.Command.*;

@Slf4j
public class WriteCommand implements Callable<ServerResponse> {
    private static final RouteHolder ROUTE_HOLDER = ServerContext.getInstance().getRouteHolder();

    private final ClientRequest request;

    private final Map<Command, Consumer<ClientRequest>> WRITE_COMMAND = new HashMap<Command, Consumer<ClientRequest>>() {{
        put(ADD_NEW_ITEM, ROUTE_HOLDER::addElement);
        put(INSERT_ITEM_AT_INDEX, ROUTE_HOLDER::insertElementAtIndex);
        put(UPDATE_ITEM_BY_ID, ROUTE_HOLDER::updateElement);
        put(REMOVE_ALL_ITEMS_BY_DISTANCE, ROUTE_HOLDER::removeElementsIfDistanceAreEqual);
        put(REMOVE_LOWER, ROUTE_HOLDER::removeElementsLessThanLower);
        put(ADD_NEW_ITEM_IF_MAX, ROUTE_HOLDER::addElementIfMax);
        put(REMOVE_ITEM_BY_ID, ROUTE_HOLDER::removeElementById);
    }};

    public WriteCommand(ClientRequest request) {
        this.request = request;
    }

    @Override
    public ServerResponse call() {
        try {
            WRITE_COMMAND.get(request.getCommand()).accept(request);
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
