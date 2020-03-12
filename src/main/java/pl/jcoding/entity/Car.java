package pl.jcoding.entity;

import lombok.Getter;
import lombok.Setter;
import pl.jcoding.util.CarUtil;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Car {

    @Id
    @Column(length = 17)
    private String vin;

    private String brand;

    private String model;

    private Long mileage;

    private LocalDate registerDate;

    private Boolean damaged;

    private Double engineCap;

    private Boolean europeSteeringWheel;

    private Double power;

    @Enumerated(EnumType.STRING)
    private CarFuel fuel;

    @PrePersist
    public void fillVin() {
        if (vin == null || vin.trim().isEmpty())
            setVin(CarUtil.tempVin());
    }

}
