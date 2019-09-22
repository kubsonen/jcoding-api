package pl.jcoding.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Data
@Entity
public class ItemCategory extends CommonEntity {

    @NotBlank
    @Length(min = 5, max = 20)
    @Column(unique = true)
    private String categoryName;

    private String categoryDescription;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private ItemCategory parent;

    @JsonIgnore
    @OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
    private Set<ItemCategory> children;

    public ItemCategory(Long id, @NotBlank @Length(min = 5, max = 20) String categoryName, String categoryDescription) {
        this.id = id;
        this.categoryName = categoryName;
        this.categoryDescription = categoryDescription;
    }
}
