package ru.itmo.model.domain;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Collection;

@Data
@Entity
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Route implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "creation_time")
    private ZonedDateTime creationTime;

    @OneToOne(mappedBy = "route")
    @ToString.Exclude
    private Coordinates coordinates;

    @OneToMany(mappedBy = "route")
    @ToString.Exclude
    private Collection<Location> locations;

    private long distance;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}