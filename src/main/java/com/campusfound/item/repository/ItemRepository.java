package com.campusfound.item.repository;

import com.campusfound.item.entity.Item;
import com.campusfound.item.entity.ItemStatus;
import com.campusfound.item.entity.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByType(ItemType type);

    List<Item> findByStatus(ItemStatus status);

    List<Item> findByCategoryIgnoreCase(String category);

    List<Item> findByTitleContainingIgnoreCase(String keyword);

}