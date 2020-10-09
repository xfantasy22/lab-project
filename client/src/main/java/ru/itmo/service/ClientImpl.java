package ru.itmo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import ru.itmo.context.ClientContext;
import ru.itmo.exception.ServerException;
import ru.itmo.exception.ValidateException;
import ru.itmo.model.ClientRequest;
import ru.itmo.model.Constant;
import ru.itmo.model.ServerResponse;
import ru.itmo.model.Status;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ThreadLocalRandom;

public class ClientImpl implements Client {
    private final DatagramSocket datagramSocket;

    @SneakyThrows
    public ClientImpl() {
        int port = ThreadLocalRandom.current().nextInt(0, 65535);
        System.out.printf("Client port: %s%n", port);
        datagramSocket = new DatagramSocket(port);
        datagramSocket.setSoTimeout(2000); // 2 sec
    }

    @Override
    @SneakyThrows
    public String sendAndReceiveData(ClientRequest data) {
        sendData(data);
        return receiveData();
    }

    public void sendData(ClientRequest object) throws Exception {
        byte[] bytes = convertToBytes(object);
        datagramSocket.send(new DatagramPacket(bytes, bytes.length, InetAddress.getLocalHost(), Constant.SERVER_PORT));
    }

    private byte[] convertToBytes(Object object) throws IllegalAccessException {
        try {
            return ClientContext.getInstance().getObjectMapper().writeValueAsBytes(object);
        } catch (Exception e) {
            throw new IllegalAccessException("Failed to serialize object of type: " + object.getClass());
        }
    }

    private String receiveData() throws SocketException {
        byte[] receiveData = new byte[datagramSocket.getReceiveBufferSize()];
        DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);
        try {
            datagramSocket.receive(packet);
            return handleResponseData(packet.getData());
        } catch (Exception e) {
            if (e instanceof ValidateException) {
                throw new ValidateException(e.getMessage());
            }
            throw new ServerException("Server is unavailable or receive timed out ");
        }
    }

    private String handleResponseData(byte[] data) throws IOException {
        ObjectMapper objectMapper = ClientContext.getInstance().getObjectMapper();
        ServerResponse serverResponse = objectMapper.readValue(data, ServerResponse.class);
        if (serverResponse.getStatus() == Status.Failed) {
            throw new ValidateException("Command failed. Error: " + serverResponse.getError());
        }
        if (serverResponse.getStatus() == Status.RequireLogin) {
            throw new ValidateException(serverResponse.getError());
        }
        return serverResponse.getResponse();
    }
}
