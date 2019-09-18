package pl.jcoding.entity;

import lombok.Data;
import pl.jcoding.util.ConverterUUIDCollection;

import javax.persistence.Convert;
import javax.persistence.Entity;
import java.util.List;
import java.util.UUID;

@Data
@Entity
public class ItemGallery extends CommonEntity {

    private UUID galleryFileName;

    private UUID mainPhotoIdentity;

    @Convert(converter = ConverterUUIDCollection.class)
    private List<UUID> photosIdentity;

}
