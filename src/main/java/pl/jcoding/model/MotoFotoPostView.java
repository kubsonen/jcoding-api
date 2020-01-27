package pl.jcoding.model;

import lombok.Data;

@Data
public class MotoFotoPostView {
    private Long profileId;
    private String profileNickname;
    private Long postId;
    private String description;
    private Boolean like;
    private String createDate;
}
