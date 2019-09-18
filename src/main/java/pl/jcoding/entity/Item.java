package pl.jcoding.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

@Data
@Entity
public class Item extends CommonEntity {

    private String itemName;

    private String description;

    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private ItemCategory category;

    @ManyToOne
    @JoinColumn(name = "gallery_id")
    private ItemGallery gallery;


}
