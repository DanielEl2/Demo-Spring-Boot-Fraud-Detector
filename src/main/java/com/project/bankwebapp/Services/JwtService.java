package com.project.bankwebapp.Services;
import com.project.bankwebapp.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService {

    // 1. A hardcoded secret key (Must be at least 32 characters long!)
    // If you restart the server, this keeps your tokens working.
    private static final String SECRET_STRING = "MySuperSecretKeyForMyBankingAppPortfolio2025!";
    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes());

    // --- GENERATE TOKEN ---
    public String generateToken(String username, Role role, UUID userId) {
        return Jwts.builder()
                //subject is the main identity of the token, while claim are additional data
                // we sign the token with our secret key to ensure the data is not tampered with
                // we compact at the end to get a string
                .setSubject(username)
                .claim("userId", userId) // Saving the UUID inside the token
                .claim("role", role)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 Hours
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    //we verify the signature with our secret key
                    // if it fails then it will throw an exception
                    // we build the parser and claim the token
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);
            return true; // Token is good!
        } catch (Exception e) {
            return false; // Token is expired or fake
        }
    }


    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    public String extractUserId(String token) {
        return extractAllClaims(token).get("userId", String.class);
    }
}