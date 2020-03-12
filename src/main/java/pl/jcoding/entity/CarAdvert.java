package pl.jcoding.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
public class CarAdvert extends Advert {

    @Id
    private String advertId;

    private Boolean ot;

    private BigDecimal price;

}
