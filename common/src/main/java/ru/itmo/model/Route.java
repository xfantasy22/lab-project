package ru.itmo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itmo.model.adapter.ZonedDateTimeAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.Objects;

@Data
@Builder(toBuilder = true)
@XmlRootElement
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class Route implements Comparable<Route>, Serializable {
    private static final long serialVersionUID = 1L;

    @XmlAttribute
    private Long id;

    @XmlElement
    private String name;

    @XmlElement
    private Coordinates coordinates;

    @XmlElement
    @XmlJavaTypeAdapter(ZonedDateTimeAdapter.class)
    private ZonedDateTime creationTime;

    @XmlElement
    private Location from;

    @XmlElement
    private Location to;

    @XmlElement
    private long distance;

    @Override
    public int compareTo(Route o) {
        if (!this.name.equals(o.getName())) {
            return this.name.compareTo(getName());
        } else if (!this.coordinates.equals(o.getCoordinates())) {
            return Objects.compare(this.coordinates, o.getCoordinates(), Comparator.comparing(Coordinates::getX).thenComparing(Coordinates::getY));
        }
        return Long.compare(this.distance, o.getDistance());
    }
}