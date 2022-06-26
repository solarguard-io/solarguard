package org.silentsoft.solarguard.util;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenUtil {

    public static final String BEARER_PREFIX = "Bearer ";

    @Value("${jwt.secret}")
    private String secret;

    public enum TokenType {
        LOGIN_ACCESS_TOKEN(1, true, 1000 * 60 * 15),               // 15 minutes
        LOGIN_REFRESH_TOKEN(2, true, 1000 * 60 * 60 * 24 * 7 * 8), // 8  weeks
        PERSONAL_ACCESS_TOKEN(3, true, 1000 * 60 * 60 * 24 * 30),  // 30 days
        PRODUCT_ACCESS_TOKEN(4, false, -1);

        private int identifier;
        private boolean hasExpiry;

        private long expiry;

        TokenType(int identifier, boolean hasExpiry, long expiry) {
            this.identifier = identifier;
            this.hasExpiry = hasExpiry;
            this.expiry = expiry;
        }

        public int identifier() {
            return identifier;
        }

        public boolean hasExpiry() {
            return hasExpiry;
        }

        public long getExpiry() {
            return expiry;
        }

        public TokenType setExpiry(long expiry) {
            this.expiry = expiry;
            return this;
        }

        public static TokenType fromIdentifier(int identifier) {
            for (TokenType tokenType : TokenType.values()) {
                if (tokenType.identifier() == identifier) {
                    return tokenType;
                }
            }
            return null;
        }

    }

    private enum Claim {
        USER_ID,
        PRODUCT_ID,
        ORGANIZATION_ID
    }

    public String removeBearerPrefix(String value) {
        if (value != null && value.startsWith(BEARER_PREFIX)) {
            return value.substring(BEARER_PREFIX.length());
        }

        return value;
    }

    public Long getUserIdFromToken(String token) {
        return getClaimValueFromToken(token, Claim.USER_ID, Long.class);
    }

    public Long getProductIdFromToken(String token) {
        return getClaimValueFromToken(token, Claim.PRODUCT_ID, Long.class);
    }

    public TokenType getTokenType(String token) {
        return TokenType.fromIdentifier(Integer.valueOf(getClaimsFromToken(token).getSubject()));
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimsFromToken(token).getExpiration();
    }

    public int getExpiryFromNow(String token) {
        return (int) ((getExpirationDateFromToken(token).getTime() - System.currentTimeMillis()) / 1000);
    }

    private <T> T getClaimValueFromToken(String token, Claim claim, Class<T> requiredType) {
        return getClaimsFromToken(token).get(claim.name(), requiredType);
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(secret.getBytes(StandardCharsets.UTF_8)).build().parseClaimsJws(token).getBody();
    }

    public boolean expiredToken(String token) {
        try {
            getClaimsFromToken(token);
        } catch (ExpiredJwtException e) {
            return true;
        }

        return false;
    }

    public boolean notExpiredToken(String token) {
        return !expiredToken(token);
    }

    public String generateLoginAccessToken(long userId) {
        Claims claims = Jwts.claims();
        claims.put(Claim.USER_ID.name(), userId);
        return generateToken(claims, TokenType.LOGIN_ACCESS_TOKEN);
    }

    public String generateLoginRefreshToken(long userId) {
        Claims claims = Jwts.claims();
        claims.put(Claim.USER_ID.name(), userId);
        return generateToken(claims, TokenType.LOGIN_REFRESH_TOKEN);
    }

    public String generatePersonalAccessToken(long userId, long expiry) {
        Claims claims = Jwts.claims();
        claims.put(Claim.USER_ID.name(), userId);
        return generateToken(claims, TokenType.PERSONAL_ACCESS_TOKEN.setExpiry(expiry));
    }

    public String generateProductAccessToken(long productId) {
        Claims claims = Jwts.claims();
        claims.put(Claim.PRODUCT_ID.name(), productId);
        return generateToken(claims, TokenType.PRODUCT_ACCESS_TOKEN);
    }

    private String generateToken(Claims claims, TokenType tokenType) {
        claims.setId(NanoIdUtils.randomNanoId());
        claims.setSubject(String.valueOf(tokenType.identifier()));
        claims.setIssuedAt(new Date(System.currentTimeMillis()));
        if (tokenType.hasExpiry()) {
            claims.setExpiration(new Date(System.currentTimeMillis() + tokenType.getExpiry()));
        }
        return Jwts.builder().setClaims(claims).signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS512).compact();
    }

}