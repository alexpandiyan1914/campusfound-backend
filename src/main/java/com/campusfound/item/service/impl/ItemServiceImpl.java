package com.campusfound.item.service.impl;

import com.campusfound.item.dto.CreateItemRequest;
import com.campusfound.item.dto.ItemResponse;
import com.campusfound.item.entity.Item;
import com.campusfound.item.entity.ItemStatus;
import com.campusfound.item.repository.ItemRepository;
import com.campusfound.item.service.ItemService;
import com.campusfound.user.entity.User;
import com.campusfound.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemResponse createItem(CreateItemRequest request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Item item = Item.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .location(request.getLocation())
                .lostFoundDate(request.getLostFoundDate())
                .status(ItemStatus.ACTIVE)
                .reportedBy(user)
                .imageUrl(request.getImageUrl())
                .build();

        Item savedItem = itemRepository.save(item);

        return mapToResponse(savedItem);
    }

    @Override
    public Page<ItemResponse> getAllItems(Pageable pageable) {

        return itemRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    public ItemResponse getItemById(Long id) {

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        return mapToResponse(item);
    }

    @Override
    public ItemResponse updateItem(
            Long id,
            CreateItemRequest request) {

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        item.setTitle(request.getTitle());
        item.setDescription(request.getDescription());
        item.setCategory(request.getCategory());
        item.setLocation(request.getLocation());
        item.setLostFoundDate(request.getLostFoundDate());
        item.setImageUrl(request.getImageUrl());

        Item updatedItem = itemRepository.save(item);

        return mapToResponse(updatedItem);
    }

    @Override
    public void deleteItem(Long id) {

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        itemRepository.delete(item);
    }

    @Override
    public List<ItemResponse> searchItems(String keyword) {

        return itemRepository
                .findByTitleContainingIgnoreCase(keyword)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ItemResponse> filterItems(
            String category,
            ItemStatus status) {

        List<Item> items;

        if (category != null && status != null) {

            /*
             * For now, repository only has individual filters.
             * We combine them in memory.
             */
            items = itemRepository.findByCategoryIgnoreCase(category)
                    .stream()
                    .filter(item -> item.getStatus() == status)
                    .toList();

        } else if (category != null) {

            items = itemRepository.findByCategoryIgnoreCase(category);

        } else if (status != null) {

            items = itemRepository.findByStatus(status);

        } else {

            items = itemRepository.findAll();
        }

        return items.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ItemResponse mapToResponse(Item item) {

        return ItemResponse.builder()
                .id(item.getId())
                .title(item.getTitle())
                .description(item.getDescription())
                .category(item.getCategory())
                .location(item.getLocation())
                .lostFoundDate(item.getLostFoundDate())
                .status(item.getStatus())
                .imageUrl(item.getImageUrl())
                .reportedBy(item.getReportedBy().getFullName())
                .createdAt(item.getCreatedAt())
                .build();
    }
}