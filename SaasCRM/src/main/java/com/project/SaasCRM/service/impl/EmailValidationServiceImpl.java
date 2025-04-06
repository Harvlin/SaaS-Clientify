package com.project.SaasCRM.service.impl;

import com.project.SaasCRM.service.EmailValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class EmailValidationServiceImpl implements EmailValidationService {

    private final RedisTemplate<String, Integer> redisTemplate;
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );
    
    private static final Pattern DOMAIN_PATTERN = Pattern.compile(
        "^[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    @Value("${app.email.rate-limit.max-per-hour}")
    private int maxEmailsPerHour = 100;

    @Value("${app.email.rate-limit.max-per-day}")
    private int maxEmailsPerDay = 1000;

    @Override
    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    @Override
    public boolean isValidDomain(String domain) {
        if (domain == null || domain.trim().isEmpty()) {
            return false;
        }
        return DOMAIN_PATTERN.matcher(domain).matches();
    }

    @Override
    public boolean isDisposableEmail(String email) {
        // This would typically check against a list of known disposable email domains
        // For now, we'll just check for common disposable email providers
        String domain = email.substring(email.indexOf("@") + 1).toLowerCase();
        return domain.contains("temporary") || 
               domain.contains("disposable") || 
               domain.contains("throwaway");
    }

    @Override
    public boolean isRateLimited(String sender) {
        String hourlyKey = "email:limit:hourly:" + sender;
        String dailyKey = "email:limit:daily:" + sender;
        
        Integer hourlyCount = redisTemplate.opsForValue().get(hourlyKey);
        Integer dailyCount = redisTemplate.opsForValue().get(dailyKey);
        
        return (hourlyCount != null && hourlyCount >= maxEmailsPerHour) ||
               (dailyCount != null && dailyCount >= maxEmailsPerDay);
    }

    @Override
    public void recordEmailSent(String sender) {
        String hourlyKey = "email:limit:hourly:" + sender;
        String dailyKey = "email:limit:daily:" + sender;
        
        redisTemplate.opsForValue().increment(hourlyKey);
        redisTemplate.opsForValue().increment(dailyKey);
        
        // Set expiry if not already set
        redisTemplate.expire(hourlyKey, Duration.ofHours(1));
        redisTemplate.expire(dailyKey, Duration.ofDays(1));
    }

    @Override
    public int getRemainingQuota(String sender) {
        String hourlyKey = "email:limit:hourly:" + sender;
        String dailyKey = "email:limit:daily:" + sender;
        
        Integer hourlyCount = redisTemplate.opsForValue().get(hourlyKey);
        Integer dailyCount = redisTemplate.opsForValue().get(dailyKey);
        
        int hourlyRemaining = maxEmailsPerHour - (hourlyCount != null ? hourlyCount : 0);
        int dailyRemaining = maxEmailsPerDay - (dailyCount != null ? dailyCount : 0);
        
        return Math.min(hourlyRemaining, dailyRemaining);
    }

    @Override
    public void resetQuota(String sender) {
        String hourlyKey = "email:limit:hourly:" + sender;
        String dailyKey = "email:limit:daily:" + sender;
        
        redisTemplate.delete(hourlyKey);
        redisTemplate.delete(dailyKey);
    }
} 