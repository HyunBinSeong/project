package com.project.my.util;

import com.project.my.dto.UserDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;  // Jakarta 패키지 사용
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${spring.jwt.secret-key}")
    private String secretKeyValue;

    @Value("${spring.jwt.token-validity}")
    private long accessTokenValidity; // Access Token 유효 기간

    @Value("${spring.jwt.refresh-token-validity}")
    private long refreshTokenValidity; // Refresh Token 유효 기간

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyValue.getBytes());
    }

    public String generateAccessToken(UserDTO userDTO) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "access"); // Access Token임을 명시
        claims.put("username", userDTO.getUsername());
        claims.put("email", userDTO.getEmail());
        return createToken(claims, userDTO.getEmail(), accessTokenValidity);
    }

    public String generateRefreshToken(UserDTO userDTO) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh"); // Refresh Token임을 명시
        claims.put("email", userDTO.getEmail()); // 필요한 최소한의 정보만 포함
        return createToken(claims, userDTO.getEmail(), refreshTokenValidity);
    }

    private String createToken(Map<String, Object> claims, String subject, long expirationTime) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateAccessToken(String token, UserDTO userDTO) {
        final String email = extractEmail(token);
        return (email.equals(userDTO.getEmail()) && !isTokenExpired(token));
    }

    public boolean validateRefreshToken(String token) {
        return !isTokenExpired(token);
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    public long getAccessTokenValidity() {
        return accessTokenValidity;
    }

    public long getRefreshTokenValidity() {
        return refreshTokenValidity;
    }
}
