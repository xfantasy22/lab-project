package ru.itmo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.Objects;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class RouteView implements Comparable<RouteView> {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private CoordinatesView coordinates;

    private ZonedDateTime creationTime;

    private LocationView from;

    private LocationView to;

    private long distance;

    @Override
    public int compareTo(RouteView o) {
        if (!this.name.equals(o.getName())) {
            return this.name.compareTo(getName());
        } else if (!this.coordinates.equals(o.getCoordinates())) {
            return Objects.compare(this.coordinates, o.getCoordinates(),
                    Comparator.comparing(CoordinatesView::getX).thenComparing(CoordinatesView::getY));
        }
        return Long.compare(this.distance, o.getDistance());
    }
}
