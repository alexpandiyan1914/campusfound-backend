package com.campusfound.item.service;

import com.campusfound.item.dto.CreateItemRequest;
import com.campusfound.item.dto.ItemResponse;
import com.campusfound.item.entity.ItemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemService {

    ItemResponse createItem(CreateItemRequest request);

    Page<ItemResponse> getAllItems(Pageable pageable);

    ItemResponse getItemById(Long id);

    ItemResponse updateItem(Long id, CreateItemRequest request);

    void deleteItem(Long id);

    List<ItemResponse> searchItems(String keyword);

    List<ItemResponse> filterItems(
            String category,
            ItemStatus status
    );
}