package pl.jcoding.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class CarAuction extends Advert {

    @Id
    @Column(length = 20)
    private String identity;

    private String identityFull;

    private LocalDateTime endAuction;

    private BigDecimal proposePrice;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn
    private Car car;

}
