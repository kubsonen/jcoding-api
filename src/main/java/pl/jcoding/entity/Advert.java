package pl.jcoding.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public class Advert {

    private String tittle;

    private String advertContent;

    private String link;

    private Boolean advertRead;

    private Boolean advertObserved;

    private Boolean advertArchived;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createObjectDate;

}
