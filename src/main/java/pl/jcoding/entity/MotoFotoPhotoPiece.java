package pl.jcoding.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor(staticName = "of")
public class MotoFotoPhotoPiece extends CommonEntity {
    public static final int MAX_PIECE_BLOB_LENGTH = 60000;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "post_id")
    private MotoFotoPost post;
    @NonNull
    private Integer ordinal;
    @Lob
    @NonNull
    @Column(name = "data_blob", length = 60000)
    private byte[] pieceData;
}

