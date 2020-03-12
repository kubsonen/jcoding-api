package pl.jcoding.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Entity
public class Item extends CommonEntity {

    @NotBlank
    @Size(min = 10, max = 100)
    private String itemName;

    @NotBlank
    @Size(max = 5000)
    private String description;


    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id")
    private ItemCategory category;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "gallery_id")
    private ItemGallery gallery;


}
