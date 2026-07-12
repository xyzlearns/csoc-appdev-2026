package com.xyz.fooddeliverybackend.repository;

import java.util.Optional;
import com.xyz.fooddeliverybackend.model.Restaurant;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RestaurantRepository
        extends MongoRepository<Restaurant, String> {
    Optional<Restaurant> findByName(String name);
}