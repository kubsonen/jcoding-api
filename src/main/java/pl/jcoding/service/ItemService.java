package pl.jcoding.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.jcoding.entity.ItemCategory;
import pl.jcoding.repository.ItemCategoryRepository;
import pl.jcoding.repository.ItemRepository;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemCategoryRepository itemCategoryRepository;

    public ItemCategory getCategoryByName(String categoryName) {
        return itemCategoryRepository.findByCategoryName(categoryName);
    }

}
