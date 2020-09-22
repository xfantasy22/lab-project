package ru.itmo.context;

import ru.itmo.service.holder.CommandInvoker;
import ru.itmo.service.holder.CommandInvokerImpl;
import ru.itmo.service.holder.RouteHolder;
import ru.itmo.service.holder.RouteHolderImpl;
import ru.itmo.service.parser.RouteParser;
import ru.itmo.service.parser.RouteParserImpl;
import ru.itmo.service.parser.xml.XmlParser;
import ru.itmo.service.parser.xml.XmlParserImpl;

public final class Context {
    private static final Context CONTEXT = new Context();
    private static final RouteHolder routeHolder = new RouteHolderImpl();
    private static final XmlParser xmlParser = new XmlParserImpl();
    private static final RouteParser routeParser = new RouteParserImpl();
    private static final CommandInvoker COMMAND_INVOKER = new CommandInvokerImpl();

    public static Context getContext() {
        return CONTEXT;
    }

    public RouteHolder getRouteHolder() {
        return routeHolder;
    }

    public XmlParser getXmlParser() {
        return xmlParser;
    }

    public RouteParser getRouteParser() {
        return routeParser;
    }

    public CommandInvoker getCommandInvoker() {
        return COMMAND_INVOKER;
    }
}