package com.xyz.fooddeliverybackend.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


import java.util.Date;

public class JwtUtil {

    private static final String SECRET =
            "fooddeliverysecretkey12345678901234567890676676767676754564634564788";

    public static String generateToken(
            String email
    ) {

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + 86400000
                        )
                )
                .signWith(
                        SignatureAlgorithm.HS256,
                        SECRET
                )
                .compact();
    }

    public static String extractEmail(
            String token
    ) {

        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}