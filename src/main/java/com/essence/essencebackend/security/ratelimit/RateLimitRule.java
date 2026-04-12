package com.essence.essencebackend.security.ratelimit;

import java.time.Duration;

public record RateLimitRule(int maxRequests, Duration window, Duration blockDuration) {
}

