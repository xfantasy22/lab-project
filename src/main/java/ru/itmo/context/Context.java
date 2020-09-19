package ru.itmo.context;

import ru.itmo.service.container.CommandContainer;
import ru.itmo.service.container.CommandContainerImpl;
import ru.itmo.service.executor.CommandExecutor;
import ru.itmo.service.executor.CommandExecutorImpl;
import ru.itmo.service.executor.script.ScriptExecutor;
import ru.itmo.service.executor.script.ScriptExecutorImpl;
import ru.itmo.service.holder.CommandHolder;
import ru.itmo.service.holder.CommandHolderImpl;
import ru.itmo.service.holder.RouteHolder;
import ru.itmo.service.holder.RouteHolderImpl;
import ru.itmo.service.parser.RouteParser;
import ru.itmo.service.parser.RouteParserImpl;
import ru.itmo.service.parser.xml.XmlParser;
import ru.itmo.service.parser.xml.XmlParserImpl;

public final class Context {
    private static final Context CONTEXT = new Context();
    private static final RouteHolder routeHolder = new RouteHolderImpl();
    private static final CommandContainer command = new CommandContainerImpl();
    private static final CommandExecutor commandExecutor = new CommandExecutorImpl();
    private static final XmlParser xmlParser = new XmlParserImpl();
    private static final RouteParser routeParser = new RouteParserImpl();
    private static final ScriptExecutor scriptExecutor = new ScriptExecutorImpl();
    private static final CommandHolder commandHolder = new CommandHolderImpl();

    public static Context getContext() {
        return CONTEXT;
    }

    public RouteHolder getRouteHolder() {
        return routeHolder;
    }

    public CommandExecutor getCommandExecutor() {
        return commandExecutor;
    }

    public CommandContainer getCommand() {
        return command;
    }

    public XmlParser getXmlParser() {
        return xmlParser;
    }

    public RouteParser getRouteParser() {
        return routeParser;
    }

    public ScriptExecutor getScriptExecutor() {
        return scriptExecutor;
    }

    public CommandHolder getCommandHolder() {
        return commandHolder;
    }

}