package com.campusfound.item.controller;

import com.campusfound.item.dto.CreateItemRequest;
import com.campusfound.item.dto.ItemResponse;
import com.campusfound.item.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.*;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ItemResponse> createItem(
            @Valid @RequestBody CreateItemRequest request) {

        ItemResponse response = itemService.createItem(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<ItemResponse>> getAllItems() {

        return ResponseEntity.ok(itemService.getAllItems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemResponse> getItemById(
            @PathVariable Long id) {

        return ResponseEntity.ok(itemService.getItemById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ItemResponse> updateItem(
            @PathVariable Long id,
            @RequestBody CreateItemRequest request) {

        return ResponseEntity.ok(itemService.updateItem(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteItem(
            @PathVariable Long id) {

        itemService.deleteItem(id);

        return ResponseEntity.ok("Item deleted successfully");
    }
}