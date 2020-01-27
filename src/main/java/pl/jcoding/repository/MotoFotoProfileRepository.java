package pl.jcoding.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import pl.jcoding.entity.MotoFotoProfile;
import pl.jcoding.entity.User;
import pl.jcoding.model.MotoFotoProfileResult;

import java.util.List;
import java.util.Optional;

public interface MotoFotoProfileRepository extends PagingAndSortingRepository<MotoFotoProfile, Long> {
    @Query("from  MotoFotoProfile mfp where mfp.id = :id")
    MotoFotoProfile findByIdentification(@Param("id") Long id);

    @Query("from  MotoFotoProfile mfp join mfp.profileOwner u where u.username = :username")
    Optional<MotoFotoProfile> findByUsername(@Param("username") String username);

    @Query("from  MotoFotoProfile mfp where mfp.profileOwner = :user")
    Optional<MotoFotoProfile> findByUser(@Param("user") User user);

    @Query("from  MotoFotoProfile mfp where mfp.nickname = :nickname")
    Optional<MotoFotoProfile> findByNickname(@Param("nickname") String nickname);

    @Query("from MotoFotoProfile mfp where mfp.nickname like concat('%',:nicknameQuery,'%')")
    List<MotoFotoProfile> searchNicknames(@Param("nicknameQuery") String nicknameQuery);

    @Query("select count(f) from MotoFotoProfile mfp left join mfp.follows f where mfp = :profile")
    Integer countFollowing(@Param("profile") MotoFotoProfile profile);

    @Query("select count(mfp) from MotoFotoProfile mfp left join mfp.follows f where f = :profile")
    Integer countFollowers(@Param("profile") MotoFotoProfile profile);

    @Query("select new pl.jcoding.model.MotoFotoProfileResult(mfp.id, mfp.name, mfp.nickname) " +
            "from MotoFotoProfile mfp where upper(mfp.nickname) like concat('%', upper(:search), '%') and mfp <> :loginProfile")
    List<MotoFotoProfileResult> getProfileResults(@Param("search") String search, @Param("loginProfile") MotoFotoProfile profile);

    @Query("select f from MotoFotoProfile mfp join mfp.follows f where f = :profile and mfp = :myProfile")
    Optional<MotoFotoProfile> findMyFollower(@Param("profile") MotoFotoProfile profile, @Param("myProfile") MotoFotoProfile myProfile);
}
