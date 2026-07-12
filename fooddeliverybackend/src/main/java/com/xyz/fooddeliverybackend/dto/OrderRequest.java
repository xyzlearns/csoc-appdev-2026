package com.xyz.fooddeliverybackend.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {

    private String restaurantId;

    private List<String> itemNames;

    private double totalAmount;
}