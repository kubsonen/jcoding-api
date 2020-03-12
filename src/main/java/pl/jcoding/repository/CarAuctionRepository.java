package pl.jcoding.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import pl.jcoding.entity.CarAuction;

import java.util.Collection;
import java.util.List;

public interface CarAuctionRepository extends CrudRepository<CarAuction, String> {
    @Query("select ca.identity from CarAuction ca where ca.identity in ?1")
    List<String> getAuctionsForIdentities(Collection<String> identities);
}
