package pl.jcoding.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import pl.jcoding.entity.ItemCategory;

import java.util.List;

public interface ItemCategoryRepository extends CrudRepository<ItemCategory, Long> {
    ItemCategory findByCategoryName (String categoryName);
    List<ItemCategory> findAllByParentIsNull();
    @Query("select new pl.jcoding.entity.ItemCategory(c.id, c.categoryName, c.categoryDescription) from ItemCategory c where c.parent.id = :parentId")
    List<ItemCategory> getNameAndDescriptionByParentId(@Param("parentId") Long parentId);
}
