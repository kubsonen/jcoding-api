package pl.jcoding.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class CommonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

//    @JsonIgnore
//    @Enumerated(EnumType.STRING)
//    protected ApiApplication apiApplication;

    @JsonIgnore
    @CreatedDate
    protected LocalDateTime createdDate;

    @JsonIgnore
    @LastModifiedDate
    protected LocalDateTime lastModifiedDate;

}
