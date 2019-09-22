package pl.jcoding.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import pl.jcoding.util.ConverterUUIDCollection;

import javax.persistence.Convert;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
public class ItemGallery extends CommonEntity {

    private UUID galleryFileName;

    private UUID mainPhotoIdentity;

    @Convert(converter = ConverterUUIDCollection.class)
    private List<UUID> photosIdentity;

    public void addPhotoIdentity(UUID photoIdentity) {
        if (photosIdentity == null) photosIdentity = new ArrayList<>();
        photosIdentity.add(photoIdentity);
    }

    public void clearPhotos() {
        if (photosIdentity != null)
            photosIdentity.clear();
    }

    @JsonIgnore
    public List<UUID> getAllPhotoIdentities() {
        List<UUID> uuids = new ArrayList<>();
        if (mainPhotoIdentity != null) uuids.add(mainPhotoIdentity);
        if (photosIdentity != null) uuids.addAll(photosIdentity);
        return uuids;
    }

}
