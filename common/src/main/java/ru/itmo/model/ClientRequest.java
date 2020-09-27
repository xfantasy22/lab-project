package ru.itmo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private Route route;
    private Command command;
    private Number argument;
}
