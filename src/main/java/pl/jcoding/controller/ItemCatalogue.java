package pl.jcoding.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.jcoding.entity.Item;
import pl.jcoding.entity.ItemCategory;
import pl.jcoding.entity.ItemGallery;
import pl.jcoding.model.ItemsResponse;
import pl.jcoding.service.ItemService;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("item-catalogue-api")
public class ItemCatalogue {

    @Autowired
    public ItemService itemService;

    @GetMapping
    public String getStarted() {
        return "My item catalogue API";
    }

    @GetMapping("/category/{category-name}")
    public ItemCategory getCategory(@PathVariable("category-name") String categoryName) {
        return itemService.getCategoryByName(categoryName);
    }

    @GetMapping("/category/children/{category-id}")
    public List<ItemCategory> getCategoryChildren(@PathVariable("category-id") String categoryId) {

        return null;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/category")
    public void saveCategory(ItemCategory itemCategory) {

    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/category")
    public void deleteCategory(ItemCategory itemCategory) {

    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/item/{category-id}")
    public ItemsResponse getItemsPage(@PathVariable("category-id") Long categoryId,
                                      @RequestAttribute(value = "query", required = false) String query, //Search field
                                      @RequestAttribute(value = "withDesc", required = false) String searchWithDescription, //Search options
                                      @PageableDefault(size = 50) Pageable pageable) {



        return new ItemsResponse();
    }

    @PostMapping("/item")
    public Item saveItem(Item item) {
        return null;
    }

    @PostMapping("/item/gallery")
    public ItemGallery createItemGallery() {
        return null;
    }

    @PostMapping("/item/gallery/add-photo")
    public ItemGallery addPhotoToGallery(ItemGallery itemGallery) {
        return null;
    }





}
