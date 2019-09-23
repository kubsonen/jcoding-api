package pl.jcoding.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.jcoding.entity.Item;
import pl.jcoding.entity.ItemCategory;
import pl.jcoding.entity.ItemGallery;
import pl.jcoding.model.ItemsResponse;
import pl.jcoding.service.ItemService;
import pl.jcoding.util.ConverterStringToItemGallery;
import pl.jcoding.util.GalleryUtil;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@CrossOrigin("*")
@RestController
@RequestMapping("item-catalogue-api")
public class ItemCatalogue {

    private static final Logger logger = LoggerFactory.getLogger(ItemCatalogue.class);

    @Autowired
    private ItemService itemService;

    @Autowired
    private GalleryUtil galleryUtil;

    @Autowired
    private ConverterStringToItemGallery converterStringToItemGallery;

    //PUBLIC ACTIONS
    @GetMapping
    public String getStarted() {
        return "My item catalogue API";
    }

    @GetMapping("/category")
    public List<ItemCategory> getStartedCategories() {
        return itemService.getStartCategories();
    }

    @GetMapping("/category/{category-name}")
    public ItemCategory getCategory(@PathVariable("category-name") String categoryName) {
        return itemService.getCategoryByName(categoryName);
    }

    @GetMapping("/category/children/{category-parent-id}")
    public List<ItemCategory> getCategoryChildren(@PathVariable("category-parent-id") Long parentId) {
        return itemService.getCategoryChildren(parentId);
    }

    @GetMapping("/item/{category-id}")
    public ItemsResponse getItemsPage(@PathVariable("category-id") Long categoryId,
                                      @RequestParam(value = "query", required = false) String query, //Search field
                                      @PageableDefault(size = 1) Pageable pageable) throws Exception {
        return itemService.getItemResponse(categoryId, query, pageable);
    }

    @GetMapping(value = "/gallery-photo/item-photo/{gallery-identity}/{photo-identity}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getGalleryPhoto(@PathVariable("gallery-identity") UUID galleryIdentity, @PathVariable("photo-identity") UUID photoIdentity) throws Exception {
        return galleryUtil.getPhotoBytesFromGallery(galleryIdentity, photoIdentity);
    }

    //ADMIN ACTION
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/category")
    public ItemCategory saveCategory(@Valid @RequestBody ItemCategory itemCategory) {
        return itemService.saveCategory(itemCategory);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/category")
    public void deleteCategory(@RequestBody ItemCategory itemCategory) {
        itemService.deleteCategory(itemCategory);
    }

    @PostMapping("/item")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Item saveItem(@Valid @RequestBody Item item) throws Exception {
        return itemService.saveItem(item);
    }

    @PostMapping("/item/gallery")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ItemGallery createItemGallery() throws Exception {
        ItemGallery itemGallery = new ItemGallery();
        itemGallery.setGalleryFileName(galleryUtil.getTempFile());
        return itemGallery;
    }

    @PostMapping(value = "/item/gallery/add-photo", consumes = {"multipart/form-data"})
    @PreAuthorize("hasAuthority('ADMIN')")
    public ItemGallery addPhotoToGallery(@RequestPart("gallery") String itemGalleryJSON, @RequestPart("file") MultipartFile file) throws Exception {
        ItemGallery itemGallery = converterStringToItemGallery.convert(itemGalleryJSON);
        if (itemGallery.getGalleryFileName() == null) throw new Exception("Not found gallery identity");
        String contentType = file.getContentType(); //Only jpg support
        if (!contentType.equals("image/jpeg")) throw new Exception("Content type is not valid");
        itemService.addPhoto(itemGallery, file.getOriginalFilename(), galleryUtil.saveMultipartToTemp(file));
        return itemGallery;
    }

}
