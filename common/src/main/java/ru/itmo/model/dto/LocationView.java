package ru.itmo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationView {
    private static final long serialVersionUID = 1L;

    private Double x;

    private Double y;

    private int z;
}
