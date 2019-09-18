package pl.jcoding.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import pl.jcoding.entity.ItemCategory;
import pl.jcoding.model.ItemsResponse;
import pl.jcoding.service.ItemService;

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

    @GetMapping("/item/{category-id}")
    public ItemsResponse getItemsPage(@PathVariable("category-id") Long categoryId,
                                      @RequestAttribute(value = "query", required = false) String query, //Search field
                                      @RequestAttribute(value = "withDesc", required = false) String searchWithDescription, //Search options
                                      @PageableDefault(size = 50) Pageable pageable) {



        return null;
    }




    
}
