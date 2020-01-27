package pl.jcoding.model;

import lombok.Data;

@Data
public class MotoFotoMember {
    private String nickname;
    private Integer posts;
    private Integer followers;
    private Integer following;
    private boolean myFollow;
}
