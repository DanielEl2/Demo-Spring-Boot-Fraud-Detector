package com.project.bankwebapp.Services;

import com.project.bankwebapp.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secretString;

    private Key secretKey;

    @PostConstruct // thiss runs after object creation and all dependecies have been already injected
    protected void init() {
        this.secretKey = Keys.hmacShaKeyFor(secretString.getBytes());
    }

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
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    //we verify the signature with our secret key
                    // if it fails then it will throw an exception
                    // we build the parser and claim the token
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true; // Token is good
        } catch (Exception e) {
            return false; // Token is expired or fake
        }
    }


    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    public String extractUserId(String token) {
        return extractAllClaims(token).get("userId", String.class);
    }
}