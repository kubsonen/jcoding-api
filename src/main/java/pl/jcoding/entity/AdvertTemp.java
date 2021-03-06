package pl.jcoding.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.UUID;

@Getter
@Setter
@Entity
public class AdvertTemp extends Advert {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

}
