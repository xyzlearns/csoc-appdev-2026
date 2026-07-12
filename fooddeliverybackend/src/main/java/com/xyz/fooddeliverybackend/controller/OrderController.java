package com.xyz.fooddeliverybackend.controller;

import com.xyz.fooddeliverybackend.dto.OrderRequest;
import com.xyz.fooddeliverybackend.model.Order;
import com.xyz.fooddeliverybackend.repository.OrderRepository;
import com.xyz.fooddeliverybackend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;

    @PostMapping
    public Order placeOrder(
            @RequestBody OrderRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {

        String token =
                authHeader.replace(
                        "Bearer ",
                        ""
                );

        String email =
                JwtUtil.extractEmail(
                        token
                );

        Order order = new Order();

        order.setRestaurantId(request.getRestaurantId());

        order.setItemNames(request.getItemNames());

        order.setTotalAmount(request.getTotalAmount());

        order.setStatus("PLACED");

        order.setCreatedAt(new Date());

        order.setUserEmail(email);

        return orderRepository.save(order);
    }

    @GetMapping
    public List<Order> getOrders(@RequestHeader("Authorization") String authHeader) {

        String token =
                authHeader.replace(
                        "Bearer ",
                        ""
                );

        String email =
                JwtUtil.extractEmail(
                        token
                );

        return orderRepository.findByUserEmail(
                email
        );
    }

}