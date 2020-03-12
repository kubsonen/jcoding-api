package pl.jcoding.repository;

import org.springframework.data.repository.CrudRepository;
import pl.jcoding.entity.CarPair;

import java.util.UUID;

public interface CarPairRepository extends CrudRepository<CarPair, UUID> {
}
