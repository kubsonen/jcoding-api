package pl.jcoding.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
public class MotoFotoPost extends CommonEntity {
    private boolean archive = false;
    private String photoDescription;
    private String photoOriginalName;
    @ManyToOne
    @JoinColumn(name="profile_id", nullable=false)
    private MotoFotoProfile profile;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id")
    private Set<MotoFotoPhotoPiece> pieces;
    @ManyToMany
    @JoinTable(name = "post_marked",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "marked_id"))
    private Set<MotoFotoProfile> marked;
    @ManyToMany
    @JoinTable(name = "post_likes",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "profile_like_id"))
    private Set<MotoFotoProfile> likes;
}
