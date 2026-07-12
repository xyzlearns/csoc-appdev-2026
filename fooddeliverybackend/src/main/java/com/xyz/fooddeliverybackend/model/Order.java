package com.xyz.fooddeliverybackend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Document(collection = "orders")
public class Order {

    @Id
    private String id;

    private String userEmail;

    private String restaurantId;

    private List<String> itemNames;

    private double totalAmount;

    private String status;

    private Date createdAt;
}