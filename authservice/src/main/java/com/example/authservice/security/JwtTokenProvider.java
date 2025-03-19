package com.example.authservice.security;


import com.example.authservice.service.UserPrincipal;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component

public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private int jwtExpirationInMs;
    private static final Logger log = Logger.getLogger(JwtTokenProvider.class);
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private static final String EMAIL_CLAIM_KEY = "email";

    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userPrincipal.getAuthorities());
        claims.put(EMAIL_CLAIM_KEY, userPrincipal.getEmail());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(Long.toString(userPrincipal.getId()))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public String getEmailFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.get(EMAIL_CLAIM_KEY, String.class);
        } catch (Exception e) {
            log.error("Could not extract email from token: {}" + e.getMessage());
            return null;
        }
    }


    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature: {}"+ ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token: {}"+ ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token: {}"+ ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token: {}"+ ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty: {}" + ex.getMessage());
        }
        return false;
    }

    public Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("Could not extract claims from token: {}" + e.getMessage());
            return Jwts.claims(); // Return an empty Claims object
        }
    }


    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            if (claims == null) return true;

            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            log.error("Error checking token expiration: {}"+ e.getMessage());
            return true;
        }
    }
}