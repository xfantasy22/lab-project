package ru.itmo;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.itmo.context.ServerContext;
import ru.itmo.model.Command;
import ru.itmo.model.Route;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ServerApplication {

    private static String name;

    @SneakyThrows
    public static void main(String[] args) {
        log.info("Server started");
        configureJackson();
        ServerContext context = ServerContext.getInstance();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        if (init(args[0], bufferedReader)) {
            boolean running = true;
            while (running) {
                try {
                    running = serverCommand(bufferedReader, context, name);
                    context.getServer().receiveAndSend();
                } catch (Exception e) {
                    log.error("Internal server error: {}", e.getMessage());
                }
            }
        }
        bufferedReader.close();
    }

    public static boolean init(String fileName, BufferedReader bufferedReader) throws IOException {
        try {
            name = fileName;
            List<Route> routeList = new ArrayList<>(ServerContext.getInstance().getXmlParser().readDataFromFile(fileName));
            ServerContext.getInstance().getRouteHolder().initCollection(routeList);
            return true;
        } catch (Exception exception) {
            log.error(exception.getMessage());
            System.out.printf("Continue with such file: %s. Y/N%n", name);
            return "y".equalsIgnoreCase(bufferedReader.readLine());
        }
    }

    private static boolean serverCommand(BufferedReader bufferedReader,
                                         ServerContext context,
                                         String fileName) throws Exception {
        if (bufferedReader.ready()) {
            String line = bufferedReader.readLine();
            if (line.isEmpty()) {
                return true;
            }
            if (Command.SAVE_COLLECTION_INTO_FILE.getCommand().equals(line)) {
                context.getRouteHolder().writeToFile(fileName);
                System.out.println("saved");
            }
            if (Command.EXIT.getCommand().equals(line)) {
                context.getRouteHolder().writeToFile(fileName);
                return false;
            }
            log.error("Wrong command: {}", line);
        }
        return true;
    }

    private static void configureJackson() {
        ServerContext.getInstance().getObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
    }
}