package ru.itmo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.itmo.model.ServerResponse;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.Future;

@Slf4j
public class DataSender implements Runnable {

    private final DatagramChannel channel;
    private final Future<ServerResponse> serverResponseFuture;
    private final SocketAddress socketAddress;

    public DataSender(DatagramChannel channel, Future<ServerResponse> serverResponseFuture, SocketAddress socketAddress) {
        this.channel = channel;
        this.serverResponseFuture = serverResponseFuture;
        this.socketAddress = socketAddress;
    }

    @Override
    @SneakyThrows
    public void run() {
        ServerResponse response = serverResponseFuture.get();
        log.info(response.toString());
        ByteBuffer byteBuffer = ByteBuffer.wrap(new ObjectMapper().writeValueAsBytes(response));
        channel.send(byteBuffer, socketAddress);
        log.info("Answer was sent to address: {}", socketAddress);
    }
}
