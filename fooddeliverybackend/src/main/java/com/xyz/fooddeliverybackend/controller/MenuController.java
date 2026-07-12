package com.xyz.fooddeliverybackend.controller;

import com.xyz.fooddeliverybackend.model.MenuItem;
import com.xyz.fooddeliverybackend.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MenuController {

    private final MenuItemRepository menuItemRepository;

    @GetMapping("/api/restaurants/{restaurantId}/menu")
    public List<MenuItem> getMenu(
            @PathVariable String restaurantId
    ) {
        return menuItemRepository.findByRestaurantId(restaurantId);
    }
}