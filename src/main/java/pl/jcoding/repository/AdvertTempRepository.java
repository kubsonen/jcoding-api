package pl.jcoding.repository;

import org.springframework.data.repository.CrudRepository;
import pl.jcoding.entity.AdvertTemp;

import java.util.UUID;

public interface AdvertTempRepository extends CrudRepository<AdvertTemp, UUID> {
}
