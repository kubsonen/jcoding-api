package pl.jcoding.repository;

import org.springframework.data.repository.CrudRepository;
import pl.jcoding.entity.CarAdvert;

public interface CarAdvertRepository extends CrudRepository<CarAdvert, String> {
}
