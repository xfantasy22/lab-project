package ru.itmo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itmo.model.domain.User;
import ru.itmo.model.dto.RouteView;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @Nullable
    private RouteView route;
    @Nonnull
    private Command command;
    @Nullable
    private Number argument;
    @Nonnull
    private User user;
}
