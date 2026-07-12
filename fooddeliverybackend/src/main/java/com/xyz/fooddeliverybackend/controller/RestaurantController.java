package com.xyz.fooddeliverybackend.controller;

import com.xyz.fooddeliverybackend.model.Restaurant;
import com.xyz.fooddeliverybackend.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantRepository restaurantRepository;

    @GetMapping("/api/restaurants")
    public List<Restaurant> getRestaurants() {

        return restaurantRepository.findAll();
    }

    @GetMapping("/api/restaurants/{id}")
    public Restaurant getRestaurant(
            @PathVariable String id
    ) {
        return restaurantRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("restaurant not found")
                );
    }
}