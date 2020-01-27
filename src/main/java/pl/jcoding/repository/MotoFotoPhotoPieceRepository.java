package pl.jcoding.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import pl.jcoding.entity.MotoFotoPhotoPiece;
import pl.jcoding.entity.MotoFotoPost;

import java.util.List;

public interface MotoFotoPhotoPieceRepository extends PagingAndSortingRepository<MotoFotoPhotoPiece, Long> {
    @Query("from MotoFotoPhotoPiece p where p.post = :post")
    List<MotoFotoPhotoPiece> getPhotoPiecesForPost(@Param("post") MotoFotoPost post);
}
