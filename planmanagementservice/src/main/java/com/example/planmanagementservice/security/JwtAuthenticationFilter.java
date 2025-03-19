package com.example.planmanagementservice.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;


import org.apache.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;



import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final String jwtSecret="e591a81e93ce45c5cb881f923596937e40f00701611dafc446e00f4aa8cdc4be20430fba3edf4184c0f9ab530f42393e7c83c6bc749851d9f23e543ce997e09a52f3b85c0ff45004fde0780c6b0d763ce5a871ea08fc75c120332d34a289674c31c09a0a780047036b8e24aea4ad403b3a19be8750cc9b75ad793c04d7bfec2bda8560bc28e09fc47b279f2845812ac1fd2c6d9240316ffc68592730a6ec8da8b6ed78e5662970b21b1b957398b3d54edbae216194cd6030301ba5f329fb45d4878d9286de39500747509db9f5e1584ddb45b12b7d4881874e31efae6797b7f63d41441e6016bce4de94d02ccac13d9f22c7732c700bb41248dd28dff76f0b8d";

    private static final Logger log = Logger.getLogger(JwtAuthenticationFilter.class);
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String authHeader = request.getHeader("Authorization");
            log.info("Auth Header: " + authHeader);

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                Claims claims = extractClaims(token);

                // Extract roles from token with the correct structure
                @SuppressWarnings("unchecked")
                List<Map<String, String>> roles = claims.get("roles", List.class);

                log.info("Extracted roles: " + roles);

                // Convert roles to Spring Security authorities
                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(role -> new SimpleGrantedAuthority(role.get("authority")))
                        .toList();

                log.info("Converted authorities: " + authorities);

                // Create authentication token with authorities
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(claims.get("email", String.class),
                                null,
                                authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Set Authentication in SecurityContext for user: {} with authorities: {}"+
                        claims.get("email")+ authorities);
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}"+ e.getMessage());
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }

    private Claims extractClaims(String token) {
        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret)); // It Decode the secret key properly
        return Jwts.parserBuilder()
                   .setSigningKey(key)  //  Using parserBuilder()
                   .build()              // Must call build()
                   .parseClaimsJws(token)
                   .getBody();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // Don't filter public endpoints
        return (path.equals("/plans") && "GET".equals(method)) ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs");
    }
}