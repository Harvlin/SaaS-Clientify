package com.project.SaasCRM.service;

public interface EmailValidationService {
    boolean isValidEmail(String email);
    
    boolean isValidDomain(String domain);
    
    boolean isDisposableEmail(String email);
    
    boolean isRateLimited(String sender);
    
    void recordEmailSent(String sender);
    
    int getRemainingQuota(String sender);
    
    void resetQuota(String sender);
} 