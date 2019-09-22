package pl.jcoding.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.jcoding.entity.Item;
import pl.jcoding.entity.ItemCategory;
import pl.jcoding.entity.ItemGallery;
import pl.jcoding.model.ItemsResponse;
import pl.jcoding.repository.ItemCategoryRepository;
import pl.jcoding.repository.ItemGalleryRepository;
import pl.jcoding.repository.ItemRepository;
import pl.jcoding.util.GalleryUtil;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ItemService {

    private static final Integer CATEGORY_NAME_MIN_LENGTH = 5;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemCategoryRepository itemCategoryRepository;

    @Autowired
    private ItemGalleryRepository itemGalleryRepository;

    @Autowired
    private GalleryUtil galleryUtil;

    public ItemCategory getCategoryByName(String categoryName) {
        return itemCategoryRepository.findByCategoryName(categoryName);
    }

    @Transactional(rollbackOn = Throwable.class)
    public ItemCategory saveCategory(ItemCategory itemCategory) {

        //Check the parameter is not null
        if (itemCategory == null) return null;

        //Initialize item category parent
        if (itemCategory.getParent() != null) {
            ItemCategory parent = itemCategory.getParent();
            if (parent.getId() != null && parent.getId() != 0l) {
                itemCategory.setParent(itemCategoryRepository.findById(parent.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Inputted parent not found")));
            }
        }

        //Check the name correction
        itemCategory
                .setCategoryName(Optional.ofNullable(itemCategory.getCategoryName())
                        .map(s -> s.trim())
                        .filter(s -> s.length() >= CATEGORY_NAME_MIN_LENGTH)
                        .orElseThrow(() -> new IllegalArgumentException("Incorrect category name")));

        return itemCategoryRepository.save(itemCategory);
    }

    public void deleteCategory(ItemCategory itemCategory) {
        if (itemCategory != null)
            Optional.ofNullable(itemCategory.getId())
                    .ifPresent(id -> this.itemCategoryRepository.deleteById(id));
    }

    @Transactional(rollbackOn = Throwable.class)
    public void addPhoto(ItemGallery itemGallery, String fileOriginalName, UUID tempFileIdentity) throws Exception {

        if (itemGallery == null) throw new Exception("Gallery not found");

        Long itemGalleryId = itemGallery.getId();
        if (itemGalleryId != null) { //Check gallery exists in the database
            itemGallery = itemGalleryRepository.findById(itemGalleryId)
                    .orElseThrow(() -> new Exception("Gallery not exists in the database"));

            UUID photoSavedToFileGallery =
                    galleryUtil.saveFileToGallery(itemGallery.getGalleryFileName(), fileOriginalName, tempFileIdentity);

            if (itemGallery.getMainPhotoIdentity() == null) {
                itemGallery.setMainPhotoIdentity(photoSavedToFileGallery);
            } else {
                itemGallery.addPhotoIdentity(photoSavedToFileGallery);
            }

            itemGalleryRepository.save(itemGallery);

        } else {

            //Save photo to the gallery
            galleryUtil.saveFileToGalleryTemp(itemGallery.getGalleryFileName(), fileOriginalName, tempFileIdentity);

            List<UUID> galleryIdentities = galleryUtil.getIdentitiesFromGalleryTempFile(itemGallery.getGalleryFileName());
            itemGallery.clearPhotos();

            UUID mainPhotoIdentity = itemGallery.getMainPhotoIdentity();
            if (mainPhotoIdentity != null) {

                //Check the assigned main photo is in the gallery file
                if (galleryIdentities.contains(mainPhotoIdentity)) {
                    galleryIdentities.remove(mainPhotoIdentity);

                    Iterator<UUID> uuidIterator = galleryIdentities.iterator();
                    while (uuidIterator.hasNext())
                        itemGallery.addPhotoIdentity(uuidIterator.next());

                } else { //If not assigned set random main photo
                    Iterator<UUID> uuidIterator = galleryIdentities.iterator();
                    itemGallery.setMainPhotoIdentity(uuidIterator.next());
                    while (uuidIterator.hasNext())
                        itemGallery.addPhotoIdentity(uuidIterator.next());
                }

            } else {

                //If main photo is not exists in the item gallery - set random
                Iterator<UUID> uuidIterator = galleryIdentities.iterator();
                itemGallery.setMainPhotoIdentity(uuidIterator.next());
                while (uuidIterator.hasNext())
                    itemGallery.addPhotoIdentity(uuidIterator.next());

            }

        }

    }

    public Item saveItem(Item item) throws Exception {

        if (item == null) throw new Exception("Item object is null");

        //Get item category
        ItemCategory itemCategory = item.getCategory();
        if (itemCategory == null || itemCategory.getId() == null) throw new Exception("Category is not inserted");
        itemCategory = itemCategoryRepository.findById(itemCategory.getId()).orElseThrow(() -> new Exception("Category not found in the database"));

        if (item.getId() != null) {
            Item itemFromDbo = itemRepository.findById(item.getId()).orElseThrow(() -> new Exception("Item not exists in the database"));
            itemFromDbo.setCategory(itemCategory);
            itemFromDbo.setItemName(item.getItemName());
            itemFromDbo.setDescription(item.getDescription());
            return itemRepository.save(itemFromDbo);
        } else {
            item.setCategory(itemCategory);
            ItemGallery itemGallery = item.getGallery();
            if (itemGallery != null) {
                if (itemGallery.getGalleryFileName() == null) throw new Exception("Gallery identity not found");
                galleryUtil.checkTempGalleryCorrection(itemGallery.getGalleryFileName(), itemGallery.getAllPhotoIdentities());
            }

            //Save item
            Item i = itemRepository.save(item);

            if (itemGallery != null) //Move created gallery to temp
                galleryUtil.moveTempFile(i.getGallery().getGalleryFileName());

            return i;
        }

    }

    public List<ItemCategory> getStartCategories() {
        return itemCategoryRepository.findAllByParentIsNull();
    }

    public ItemsResponse getItemResponse(Long categoryId, String query, Pageable pageable) throws Exception {

        ItemCategory itemCategory = itemCategoryRepository.findById(categoryId).orElseThrow(() -> new Exception("Category not found"));

        Page<Item> items;
        if (query != null && !query.isEmpty()) {
            items = itemRepository.findItems(query, itemCategory, pageable);
        } else {
            items = itemRepository.findItems(itemCategory, pageable);
        }

        ItemsResponse itemsResponse = new ItemsResponse();
        itemsResponse.setResponseDate(LocalDateTime.now());
        itemsResponse.setItems(items.getContent());
        itemsResponse.setCategory(itemCategory);
        itemsResponse.setCountOfPages(items.getTotalPages());
        itemsResponse.setCurrentPage(items.getNumber() + 1);

        return itemsResponse;
    }

    @Transactional
    public List<ItemCategory> getCategoryChildren(Long parentId) {
        return itemCategoryRepository.getNameAndDescriptionByParentId(parentId);
    }

}
