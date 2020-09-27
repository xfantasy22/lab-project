package ru.itmo.service;

import ru.itmo.model.ClientRequest;

public interface Client {

    String sendAndReceiveData(ClientRequest data);

}
