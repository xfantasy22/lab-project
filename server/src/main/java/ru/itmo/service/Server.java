package ru.itmo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.itmo.context.ServerContext;
import ru.itmo.dao.UserDao;
import ru.itmo.exception.ValidateException;
import ru.itmo.model.*;
import ru.itmo.model.domain.User;
import ru.itmo.service.command.ReadCommand;
import ru.itmo.service.command.WriteCommand;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class Server implements Runnable {
    private final DatagramChannel datagramChannel;
    private boolean running = true;

    @SneakyThrows
    public Server() {
        datagramChannel = DatagramChannel.open();
        datagramChannel.configureBlocking(false);
        datagramChannel.bind(new InetSocketAddress(InetAddress.getLocalHost(), Constant.SERVER_PORT));
        log.info("Server host: {}, port: {}", InetAddress.getLocalHost().getHostName(), Constant.SERVER_PORT);
    }

    @Override
    @SneakyThrows
    public void run() {
        while (running) {
            try {
                Map<SocketAddress, ClientRequest> receiveData = receiveData();
                if (receiveData != null) {
                    receiveData.forEach((key, value) -> sendData(value, key));
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    private Map<SocketAddress, ClientRequest> receiveData() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(2048);
        SocketAddress address = datagramChannel.receive(buffer);
        if (address == null) {
            return null;
        }

        log.info("Connection established, address: {}", address);
        ((Buffer) buffer).flip();
        ClientRequest request = new ObjectMapper().readValue(buffer.array(), ClientRequest.class);
        ((Buffer) buffer).clear();
        return Collections.singletonMap(address, request);
    }


    private void sendData(ClientRequest request, SocketAddress socketAddress) {
        ExecutorService fixed = Executors.newFixedThreadPool(5);
        ExecutorService cache = Executors.newCachedThreadPool();

        Future<ServerResponse> future;
        log.info("Command request: {}", request.getCommand());
        try {
            if (Command.SIGN_UP == request.getCommand()) {
                cache.submit(new DataSender(datagramChannel, fixed.submit(() -> createUser(request)), socketAddress));
                return;
            }

            if (Command.SIGN_IN == request.getCommand()) {
                cache.submit(new DataSender(datagramChannel, fixed.submit(() -> validateUser(request)), socketAddress));
                return;
            }

            ServerResponse errorResponse = validateUser(request);
            if (Status.Failed == errorResponse.getStatus()) {
                cache.submit(new DataSender(datagramChannel, fixed.submit(() -> errorResponse), socketAddress));
                return;
            }

            Callable<ServerResponse> responseCallable;
            if (Command.isWriteCommand(request.getCommand())) {
                responseCallable = new WriteCommand(request);
            } else {
                responseCallable = new ReadCommand(request);
            }
            future = fixed.submit(responseCallable);
        } catch (Exception e) {
            ServerResponse error = ServerResponse.builder().error(e.getMessage()).status(Status.Failed).build();
            future = fixed.submit(() -> error);
        }
        cache.submit(new DataSender(datagramChannel, future, socketAddress));
    }


    private ServerResponse createUser(ClientRequest request) {
        UserDao userDao = ServerContext.getInstance().getUserDao();
        User user = request.getUser();
        if (userDao.findOne(user.getUsername()) != null) {
            throw new ValidateException("Username should be unique");
        }

        user.setPassword(encrypt(user.getPassword()));
        userDao.save(user);

        return ServerResponse.builder()
                .status(Status.Success)
                .response("User successfully created")
                .build();
    }

    private ServerResponse validateUser(ClientRequest request) {
        User user = request.getUser();
        User userFromDb = ServerContext.getInstance().getUserDao().findOne(user.getUsername());
        if (userFromDb != null) {
            if (checkPassword(userFromDb.getPassword(), encrypt(user.getPassword()))) {
                request.setUser(userFromDb);
                return ServerResponse.builder()
                        .status(Status.Success)
                        .response("You are successfully logged in")
                        .build();
            }
        }
        throw new ValidateException("Username or password is not valid");
    }

    @SneakyThrows
    private String encrypt(String input) {
        MessageDigest md = MessageDigest.getInstance("SHA-384");
        byte[] messageDigest = md.digest(input.getBytes());
        BigInteger no = new BigInteger(1, messageDigest);
        StringBuilder hash = new StringBuilder(no.toString(16));
        while (hash.length() < 32) {
            hash.insert(0, "0");
        }
        return hash.toString();
    }

    private boolean checkPassword(String expected, String actual) {
        return expected.equals(actual);
    }

    public void stopServer() {
        running = false;
    }
}
