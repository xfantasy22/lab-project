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
public class ServerResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private String error;
    private String response;
    private Status status;
}
