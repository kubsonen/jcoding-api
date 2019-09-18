package pl.jcoding.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
public class ItemCategory extends CommonEntity {

    private String categoryName;

    private String categoryDescription;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private ItemCategory parent;

    @JsonIgnore
    @OneToMany(mappedBy = "parent")
    private Set<ItemCategory> children;


}
