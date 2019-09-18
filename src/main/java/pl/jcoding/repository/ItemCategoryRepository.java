package pl.jcoding.repository;

import org.springframework.data.repository.CrudRepository;
import pl.jcoding.entity.ItemCategory;

public interface ItemCategoryRepository extends CrudRepository<ItemCategory, Long> {
    ItemCategory findByCategoryName (String categoryName);
}
