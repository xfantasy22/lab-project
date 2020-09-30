package ru.itmo.model;

import com.sun.istack.Nullable;
import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @Nullable
    private Route route;
    @NonNull
    private Command command;
    @Nullable
    private Number argument;
}
