package pl.jcoding.model;

import lombok.Data;
import pl.jcoding.entity.Item;
import pl.jcoding.entity.ItemCategory;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemItemsResponse {

    private LocalDateTime responseDate;

    private Integer currentPage;

    private Integer countOfPages;

    private ItemCategory category;

    private List<Item> items;

}
