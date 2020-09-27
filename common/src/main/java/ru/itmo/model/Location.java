package ru.itmo.model;

import lombok.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@Data
@Builder
@XmlRootElement
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class Location implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlElement
    private Double x;

    @XmlElement
    private Double y;

    @XmlElement
    private int z;
}
