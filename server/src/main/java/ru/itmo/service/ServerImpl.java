package ru.itmo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.itmo.context.ServerContext;
import ru.itmo.model.ClientRequest;
import ru.itmo.model.Command;
import ru.itmo.model.Constant;
import ru.itmo.model.ServerResponse;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Collections;
import java.util.Map;

@Slf4j
public class ServerImpl implements Server {
    private final DatagramChannel datagramChannel;

    @SneakyThrows
    public ServerImpl() {
        datagramChannel = DatagramChannel.open();
        datagramChannel.configureBlocking(false);
        datagramChannel.bind(new InetSocketAddress(InetAddress.getLocalHost(), Constant.SERVER_PORT));
        log.info("Server host: {}, port: {}", InetAddress.getLocalHost().getHostName(), Constant.SERVER_PORT);
    }


    @Override
    @SneakyThrows
    public void receiveAndSend() {
        ObjectMapper objectMapper = ServerContext.getInstance().getObjectMapper();
        Map<SocketAddress, ClientRequest> receiveData = receiveData(objectMapper);
        if (receiveData == null) {
            return;
        }
        receiveData.forEach((key, value) -> sendData(objectMapper, value, key));
    }

    private Map<SocketAddress, ClientRequest> receiveData(ObjectMapper objectMapper) throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(2048);
        SocketAddress address = datagramChannel.receive(buffer);
        if (address == null) {
            return null;
        }
        log.info("Connection established, address: {}", address);
        ((Buffer)buffer).flip();
        ClientRequest request = objectMapper.readValue(buffer.array(), ClientRequest.class);
        ((Buffer)buffer).clear();
        return Collections.singletonMap(address, request);
    }

    private void sendData(ObjectMapper mapper, ClientRequest request, SocketAddress socketAddress) {
        try {
            ServerContext context = ServerContext.getInstance();
            ServerResponse serverResponse;
            log.info("Command request: {}", request.getCommand());
            if (Command.isWriteCommand(request.getCommand())) {
                serverResponse = context.getWriteCommand().getResult(request);
            } else {
                serverResponse = context.getReadCommand().getResult(request);
            }
            ByteBuffer byteBuffer = ByteBuffer.wrap(mapper.writeValueAsBytes(serverResponse));
            datagramChannel.send(byteBuffer, socketAddress);
            log.info("Answer was sent to address: {}", socketAddress);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
