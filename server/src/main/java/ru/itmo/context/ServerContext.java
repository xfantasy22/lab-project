package ru.itmo.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Value;
import ru.itmo.dao.RouteDao;
import ru.itmo.dao.RouteDaoImpl;
import ru.itmo.dao.UserDao;
import ru.itmo.dao.UserDaoImpl;
import ru.itmo.service.holder.RouteHolder;
import ru.itmo.service.holder.RouteHolderImpl;

@Value
public class ServerContext {
    private static volatile ServerContext SERVER_CONTEXT;
    private static final RouteHolder ROUTE_HOLDER = new RouteHolderImpl();
    private static final RouteDao ROUTE_DAO = new RouteDaoImpl();
    private static final UserDao USER_DAO = new UserDaoImpl();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static ServerContext getInstance() {
        ServerContext context = SERVER_CONTEXT;
        if (context == null) {
            synchronized (ServerContext.class) {
                context = SERVER_CONTEXT;
                if (context == null) {
                    context = SERVER_CONTEXT = new ServerContext();
                }
            }
        }
        return context;
    }

    public RouteHolder getRouteHolder() {
        return ROUTE_HOLDER;
    }

    public ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    public RouteDao getRouteDao() {
        return ROUTE_DAO;
    }

    public UserDao getUserDao() {
        return USER_DAO;
    }
}