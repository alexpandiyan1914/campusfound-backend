package com.campusfound.item.service;

import com.campusfound.item.dto.CreateItemRequest;
import com.campusfound.item.dto.ItemResponse;

public interface ItemService {

    ItemResponse createItem(CreateItemRequest request);

}