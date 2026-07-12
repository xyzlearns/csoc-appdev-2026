package com.xyz.fooddeliverybackend.repository;

import com.xyz.fooddeliverybackend.model.MenuItem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MenuItemRepository
        extends MongoRepository<MenuItem, String> {

    List<MenuItem> findByRestaurantId(String restaurantId);
}