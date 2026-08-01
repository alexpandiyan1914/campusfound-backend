package com.campusfound.item.service;

import com.campusfound.item.dto.CreateItemRequest;
import com.campusfound.item.dto.ItemResponse;
import java.util.List;

public interface ItemService {

    ItemResponse createItem(CreateItemRequest request);

    List<ItemResponse> getAllItems();

    ItemResponse getItemById(Long id);

    ItemResponse updateItem(Long id, CreateItemRequest request);

    void deleteItem(Long id);

}