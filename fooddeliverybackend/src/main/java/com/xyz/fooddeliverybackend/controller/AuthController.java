package com.xyz.fooddeliverybackend.controller;

import com.xyz.fooddeliverybackend.dto.LoginRequest;
import com.xyz.fooddeliverybackend.dto.LoginResponse;
import com.xyz.fooddeliverybackend.dto.RegisterRequest;
import com.xyz.fooddeliverybackend.dto.UpdateProfileRequest;
import com.xyz.fooddeliverybackend.dto.UserResponse;
import com.xyz.fooddeliverybackend.model.User;
import com.xyz.fooddeliverybackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.xyz.fooddeliverybackend.util.JwtUtil;

import java.util.Date;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;

    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException(
                    "Email already exists"
            );
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());


        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        user.setPasswordHash(encoder.encode(request.getPassword()));

        user.setCreatedAt(new Date());

        return userRepository.save(user);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        ));

        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder();

        boolean matches =
                encoder.matches(
                        request.getPassword(),
                        user.getPasswordHash()
                );

        if (!matches) {
            throw new RuntimeException(
                    "Invalid password"
            );
        }

        String token = JwtUtil.generateToken(user.getEmail());

        return new LoginResponse(
                token,
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    @GetMapping("/me")
    public UserResponse me(
            @RequestHeader("Authorization")
            String authHeader
    ) {
        System.out.println(authHeader);

        String token =
                authHeader.replace(
                        "Bearer ",
                        ""
                );

        String email =
                JwtUtil.extractEmail(token);

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                ));

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    @PutMapping("/me")
    public UserResponse updateMe(
            @RequestHeader("Authorization")
            String authHeader,
            @RequestBody UpdateProfileRequest request
    ) {
        String token =
                authHeader.replace(
                        "Bearer ",
                        ""
                );

        String email =
                JwtUtil.extractEmail(token);

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                ));

        if (request.getName() != null
                && !request.getName().isBlank()) {
            user.setName(request.getName().trim());
        }

        User saved = userRepository.save(user);

        return new UserResponse(
                saved.getId(),
                saved.getName(),
                saved.getEmail()
        );
    }
}
