package pl.jcoding.repository;

import org.springframework.data.repository.CrudRepository;
import pl.jcoding.entity.ItemGallery;

public interface ItemGalleryRepository extends CrudRepository<ItemGallery, Long> {
}
