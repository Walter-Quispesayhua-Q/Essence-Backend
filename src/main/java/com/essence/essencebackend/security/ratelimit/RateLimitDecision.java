package com.essence.essencebackend.security.ratelimit;

public record RateLimitDecision(boolean isAllowed, long retryAfterSeconds) {

    public static RateLimitDecision permit() {
        return new RateLimitDecision(true, 0);
    }

    public static RateLimitDecision deny(long retryAfterSeconds) {
        return new RateLimitDecision(false, retryAfterSeconds);
    }
}



