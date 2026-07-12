package com.xyz.fooddeliverybackend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "users")
@Data
public class User {

    @Id
    private String id;

    private String name;

    private String email;

    private String passwordHash;

    private Date createdAt;
}