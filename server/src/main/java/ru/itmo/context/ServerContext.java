package ru.itmo.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Value;
import ru.itmo.service.Server;
import ru.itmo.service.ServerImpl;
import ru.itmo.service.command.ReadCommand;
import ru.itmo.service.command.WriteCommand;
import ru.itmo.service.holder.RouteHolder;
import ru.itmo.service.holder.RouteHolderImpl;
import ru.itmo.service.xml.XmlParser;
import ru.itmo.service.xml.XmlParserImpl;

@Value
public class ServerContext {
    private static final ServerContext SERVER_CONTEXT = new ServerContext();
    private static final RouteHolder ROUTE_HOLDER = new RouteHolderImpl();
    private static final XmlParser XML_PARSER = new XmlParserImpl();
    private static final Server SERVER = new ServerImpl();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final ReadCommand READ_COMMAND = new ReadCommand();
    private static final WriteCommand WRITE_COMMAND = new WriteCommand();

    public static ServerContext getInstance() {
        return SERVER_CONTEXT;
    }

    public RouteHolder getRouteHolder() {
        return ROUTE_HOLDER;
    }

    public XmlParser getXmlParser() {
        return XML_PARSER;
    }

    public Server getServer() {
        return SERVER;
    }

    public ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    public ReadCommand getReadCommand() {
        return READ_COMMAND;
    }

    public WriteCommand getWriteCommand() {
        return WRITE_COMMAND;
    }
}