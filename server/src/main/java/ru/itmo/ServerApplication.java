package ru.itmo;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import ru.itmo.context.ServerContext;
import ru.itmo.model.Command;
import ru.itmo.service.Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static ru.itmo.HibernateUtils.*;

@Slf4j
public class ServerApplication {

    @SneakyThrows
    public static void main(String[] args) {
        // db migration
        Flyway flyway = Flyway.configure().dataSource("jdbc:postgresql://" + HOST + ":5432/" + DATABASE_NAME, USERNAME, PASSWORD).load();
        String systemProperties = System.getProperty("clean", "false");
        if (Boolean.parseBoolean(systemProperties)) {
            flyway.clean();
            log.info("Migrations cleaned successfully");
            return;
        } else {
            flyway.migrate();
        }

        //config jackson
        configureJackson();

        ServerContext.getInstance().getRouteHolder().initCollection();
        //program
        Server server = new Server();
        new Thread(server).start();
        log.info("Server started");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        boolean running = true;
        while (running) {
            try {
                running = serverCommand(bufferedReader);
                if (!running) {
                    server.stopServer();
                }
            } catch (Exception e) {
                log.error("Internal server error: {}", e.getMessage());
            }
        }
        bufferedReader.close();
    }

    private static boolean serverCommand(BufferedReader bufferedReader) throws Exception {
        if (bufferedReader.ready()) {
            String line = bufferedReader.readLine();
            if (line.isEmpty()) {
                return true;
            }
            return handleServerCommands(line);
        }
        return true;
    }

    private static boolean handleServerCommands(String command) {
        if (Command.EXIT.getCommand().equals(command)) {
            return false;
        }
        log.error("Wrong command: {}", command);
        return true;
    }

    private static void configureJackson() {
        ServerContext.getInstance().getObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
    }
}