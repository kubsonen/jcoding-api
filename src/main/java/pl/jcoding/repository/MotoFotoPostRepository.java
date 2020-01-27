package pl.jcoding.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import pl.jcoding.entity.MotoFotoPost;
import pl.jcoding.entity.MotoFotoProfile;

import java.util.List;

public interface MotoFotoPostRepository extends PagingAndSortingRepository<MotoFotoPost, Long> {
    @Query("select p from MotoFotoPost p where p.profile <> :myProfile and p.archive = false order by p.createdDate desc")
    List<MotoFotoPost> getPostsForBoard(@Param("myProfile") MotoFotoProfile myProfile, Pageable pageable);

    @Query("select p from MotoFotoPost p where p.profile = :profile and p.archive = false order by p.createdDate desc")
    List<MotoFotoPost> getPostsForProfile(@Param("profile") MotoFotoProfile profile);

    @Query("select p from MotoFotoPost p where p.id = :id  and p.archive = false")
    MotoFotoPost getPost(@Param("id") Long id);

    @Query("select count(mfp) from MotoFotoPost mfp where mfp.profile = :profile  and mfp.archive = false")
    Integer postsCount(@Param("profile") MotoFotoProfile profile);

    @Query("select mfp.id from MotoFotoPost  mfp join mfp.likes l where l = :profile")
    List<Long> getByProfileLikePosts(@Param("profile") MotoFotoProfile profile);

}
