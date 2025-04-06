package com.project.SaasCRM.security;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import java.util.concurrent.ConcurrentHashMap;
import java.time.Instant;

@Component
public class TokenBlacklist {
    private final ConcurrentHashMap<String, Instant> blacklistedTokens = new ConcurrentHashMap<>();
    
    @Value("${app.jwt.expiration}")
    private long jwtExpirationInMs;

    public void blacklist(String token) {
        blacklistedTokens.put(token, Instant.now().plusMillis(jwtExpirationInMs));
    }

    public boolean isBlacklisted(String token) {
        Instant expiryTime = blacklistedTokens.get(token);
        if (expiryTime == null) {
            return false;
        }
        
        if (Instant.now().isAfter(expiryTime)) {
            blacklistedTokens.remove(token);
            return false;
        }
        
        return true;
    }

    // Cleanup method to remove expired tokens (can be scheduled)
    public void cleanupExpiredTokens() {
        Instant now = Instant.now();
        blacklistedTokens.entrySet().removeIf(entry -> now.isAfter(entry.getValue()));
    }
} 