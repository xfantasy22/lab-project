package ru.itmo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoordinatesView {
    private static final long serialVersionUID = 1L;

    private Long x;

    private float y;
}
