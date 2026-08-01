package com.campusfound.item.service.impl;

import com.campusfound.item.dto.CreateItemRequest;
import com.campusfound.item.dto.ItemResponse;
import com.campusfound.item.entity.Item;
import com.campusfound.item.entity.ItemStatus;
import com.campusfound.item.entity.ItemType;
import com.campusfound.item.repository.ItemRepository;
import com.campusfound.item.service.ItemService;
import com.campusfound.user.entity.User;
import com.campusfound.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemResponse createItem(CreateItemRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Item item = Item.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .location(request.getLocation())
                .lostFoundDate(request.getLostFoundDate())
                .type(request.getType())
                .status(ItemStatus.OPEN)
                .reportedBy(user)
                .build();

        Item savedItem = itemRepository.save(item);

        return ItemResponse.builder()
                .id(savedItem.getId())
                .title(savedItem.getTitle())
                .description(savedItem.getDescription())
                .category(savedItem.getCategory())
                .location(savedItem.getLocation())
                .lostFoundDate(savedItem.getLostFoundDate())
                .type(savedItem.getType())
                .status(savedItem.getStatus())
                .imageUrl(savedItem.getImageUrl())
                .reportedBy(user.getFullName())
                .createdAt(savedItem.getCreatedAt())
                .build();
    }

    @Override
    public List<ItemResponse> getAllItems() {

        return itemRepository.findAll()
                .stream()
                .map(item -> ItemResponse.builder()
                        .id(item.getId())
                        .title(item.getTitle())
                        .description(item.getDescription())
                        .category(item.getCategory())
                        .location(item.getLocation())
                        .lostFoundDate(item.getLostFoundDate())
                        .type(item.getType())
                        .status(item.getStatus())
                        .imageUrl(item.getImageUrl())
                        .reportedBy(item.getReportedBy().getFullName())
                        .createdAt(item.getCreatedAt())
                        .build())
                .toList();
    }

    @Override
    public ItemResponse getItemById(Long id) {

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        return ItemResponse.builder()
                .id(item.getId())
                .title(item.getTitle())
                .description(item.getDescription())
                .category(item.getCategory())
                .location(item.getLocation())
                .lostFoundDate(item.getLostFoundDate())
                .type(item.getType())
                .status(item.getStatus())
                .imageUrl(item.getImageUrl())
                .reportedBy(item.getReportedBy().getFullName())
                .createdAt(item.getCreatedAt())
                .build();
    }

    @Override
    public ItemResponse updateItem(Long id, CreateItemRequest request) {

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        item.setTitle(request.getTitle());
        item.setDescription(request.getDescription());
        item.setCategory(request.getCategory());
        item.setLocation(request.getLocation());
        item.setLostFoundDate(request.getLostFoundDate());
        item.setType(request.getType());

        Item updatedItem = itemRepository.save(item);

        return ItemResponse.builder()
                .id(updatedItem.getId())
                .title(updatedItem.getTitle())
                .description(updatedItem.getDescription())
                .category(updatedItem.getCategory())
                .location(updatedItem.getLocation())
                .lostFoundDate(updatedItem.getLostFoundDate())
                .type(updatedItem.getType())
                .status(updatedItem.getStatus())
                .imageUrl(updatedItem.getImageUrl())
                .reportedBy(updatedItem.getReportedBy().getFullName())
                .createdAt(updatedItem.getCreatedAt())
                .build();
    }

    @Override
    public void deleteItem(Long id) {

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        itemRepository.delete(item);
    }

    private ItemResponse mapToResponse(Item item) {

        return ItemResponse.builder()
                .id(item.getId())
                .title(item.getTitle())
                .description(item.getDescription())
                .category(item.getCategory())
                .location(item.getLocation())
                .lostFoundDate(item.getLostFoundDate())
                .type(item.getType())
                .status(item.getStatus())
                .imageUrl(item.getImageUrl())
                .reportedBy(item.getReportedBy().getFullName())
                .createdAt(item.getCreatedAt())
                .build();
    }

    @Override
    public List<ItemResponse> searchItems(String keyword) {

        return itemRepository.findByTitleContainingIgnoreCase(keyword)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ItemResponse> filterItems(
            String category,
            ItemType type,
            ItemStatus status) {

        List<Item> items;

        if (category != null) {
            items = itemRepository.findByCategoryIgnoreCase(category);
        }
        else if (type != null) {
            items = itemRepository.findByType(type);
        }
        else if (status != null) {
            items = itemRepository.findByStatus(status);
        }
        else {
            items = itemRepository.findAll();
        }

        return items.stream()
                .map(this::mapToResponse)
                .toList();
    }
}