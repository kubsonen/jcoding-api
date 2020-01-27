package pl.jcoding.entity;

import lombok.Getter;
import lombok.Setter;
import pl.jcoding.model.ApiSex;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Person extends CommonEntity {
    private String name;
    private String secondName;
    private String surname;
    private Integer age;
    @Enumerated(EnumType.STRING)
    private ApiSex sex;
}
