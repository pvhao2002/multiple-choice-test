package com.app.quizzservice.jwt;

import com.app.quizzservice.model.User;
import com.app.quizzservice.model.UserToken;
import com.app.quizzservice.utils.Constants;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.wildfly.common.annotation.NotNull;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;


@Component
@Log
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    //Thời gian có hiệu lực của chuỗi jwt
    @Value("${jwt.access-ttl}")
    private Duration jwtExpiration;

    public String extractUsername(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (Exception e) {
            log.warning("Token exception: " + e.getMessage());
            return null;
        }
    }

    public String extractPassword(String token) {
        return extractClaim(token, claims -> (String) claims.get("password"));
    }

    public List<String> extractRoles(String token) {
        return Collections.singletonList(extractClaim(token, claims -> claims
                .get("role")
                .toString()));
    }

    public Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String email = extractUsername(token);
        final String password = extractPassword(token);
        return (email.equals(userDetails.getUsername())
                && password.equals(userDetails.getPassword())
                && !isTokenExpired(token));
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(String email, String password, List<String> roles) {
        var issuedAt = new Date();
        var expiration = new Date(issuedAt.getTime() + jwtExpiration.toMillis());
        return generateToken(email, password, roles, issuedAt, expiration);
    }

    public String generateToken(String email, String password, String role) {
        return generateToken(email, password, Collections.singletonList(role));
    }

    public String generateToken(Optional<User> user) {
        return user.map(value -> generateToken(
                value.getEmail(),
                value.getPassword(),
                Collections.singletonList(value.getRole().name())
        )).orElse(null);
    }

    public String generateToken(String email, String password, List<String> roles, Date issueAt, Date expiration) {
        return Jwts
                .builder()
                .setSubject(email)
                .claim("role", roles)
                .claim("password", password)
                .setIssuedAt(issueAt)
                .setExpiration(expiration)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public UserToken generateToken(User user) {
        var issuedAt = new Date();
        var expiration = new Date(issuedAt.getTime() + jwtExpiration.toMillis());
        var token = generateToken(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(user.getRole().name()),
                issuedAt,
                expiration
        );
        return UserToken.builder()
                        .token(token)
                        .userId(user.getUserId())
                        .createdAt(issuedAt)
                        .expiredAt(expiration)
                        .build();
    }

    public boolean validateToken(String token) {
        try {
            Jwts
                    .parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException |
                 IllegalArgumentException ignored) {
        }
        return false;
    }

    public String getToken(HttpServletRequest request) {
        final String bearerToken = request.getHeader(Constants.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(Constants.BEARER)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
