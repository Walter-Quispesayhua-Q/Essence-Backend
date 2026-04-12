package com.essence.essencebackend.security.ratelimit;

public interface RateLimitService {

    RateLimitDecision tryConsume(String key, RateLimitRule rule);

    void assertLoginIdentityAllowed(String email);

    void recordLoginFailure(String email);

    void clearLoginFailures(String email);
}

