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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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
}