package pl.jcoding.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"brand", "model"}))
public class CarPair {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String brand;
    private String model;
    private LocalDateTime lastUpdate;
}
