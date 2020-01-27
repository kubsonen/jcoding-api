package pl.jcoding.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
public class MotoFotoProfile extends CommonEntity {
    @Column(name = "name_surname")
    private String name;
    @Column(unique = true)
    private String nickname;
    @JsonIgnore
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id", unique = true)
    private User profileOwner;
    @OneToMany
    @JoinColumn(name = "profile_id")
    private Set<MotoFotoPost> posts;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "profile_id")
    private Set<MotoFotoPhotoPiece> profilePhotoPieces;
    @ManyToMany
    @JoinTable(name = "profile_follows",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "follower_id"))
    private Set<MotoFotoProfile> follows;
}
