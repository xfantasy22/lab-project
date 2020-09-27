package ru.itmo.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Value;
import ru.itmo.service.*;

@Value
public class ClientContext {
    private static final ClientContext CLIENT_CONTEXT = new ClientContext();
    private static final Client DATA_RECEIVER = new ClientImpl();
    private static final CommandInvoker COMMAND_INVOKER = new CommandInvokerImpl();
    private static final ScriptExecutor SCRIPT_EXECUTOR = new ScriptExecutorImpl();
    private static final RouteCreator ROUTE_CREATOR = new RouteCreatorImpl();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static ClientContext getInstance() {
        return CLIENT_CONTEXT;
    }

    public CommandInvoker getCommandInvoker() {
        return COMMAND_INVOKER;
    }

    public Client getClient() {
        return DATA_RECEIVER;
    }

    public ScriptExecutor getScriptExecutor() {
        return SCRIPT_EXECUTOR;
    }

    public RouteCreator getRouteCreator() {
        return ROUTE_CREATOR;
    }

    public ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }
}
