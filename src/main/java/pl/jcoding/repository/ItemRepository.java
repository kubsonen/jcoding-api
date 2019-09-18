package pl.jcoding.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import pl.jcoding.entity.Item;

public interface ItemRepository extends PagingAndSortingRepository<Item, Long> {
}
