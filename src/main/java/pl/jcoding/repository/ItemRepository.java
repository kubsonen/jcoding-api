package pl.jcoding.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import pl.jcoding.entity.Item;
import pl.jcoding.entity.ItemCategory;

public interface ItemRepository extends PagingAndSortingRepository<Item, Long> {
    @Query(value = "select i from Item i where i.category = :itemCategory")
    Page<Item> findItems(@Param("itemCategory") ItemCategory itemCategory, Pageable pageable);

    @Query(value = "select i from Item i where i.itemName like concat('%',:itemName,'%') and i.category = :itemCategory")
    Page<Item> findItems(@Param("itemName") String itemName, @Param("itemCategory") ItemCategory itemCategory, Pageable pageable);
}
