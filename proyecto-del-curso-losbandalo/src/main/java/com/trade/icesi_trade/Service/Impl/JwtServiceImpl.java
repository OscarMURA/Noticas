package com.trade.icesi_trade.Service.Impl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtServiceImpl {

    private static final String SECRET_KEY = "p5rT9$wKm3#sV1q8ZbX4Lk2!uYhEjR6M"; 
    private static final long EXPIRATION_TIME = 1000 * 60 * 60;

    private final UserDetailsService userDetailsService;

    public JwtServiceImpl (UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    public UserDetails getUserDetailsFromToken(String token) {
        String username = extractUsername(token);
        return userDetailsService.loadUserByUsername(username);
    }

    public boolean isTokenValid(String token) {
        return ( !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }    

    public long getTokenCreationTime(String token) {
        return extractClaim(token, Claims::getIssuedAt).getTime();
    }

    public long getTokenExpirationTime(String token) {
        return extractClaim(token, Claims::getExpiration).getTime();
    }
}