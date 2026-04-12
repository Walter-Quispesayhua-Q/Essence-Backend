package com.essence.essencebackend.security.ratelimit;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class InMemoryRateLimitService implements RateLimitService {

    private static final RateLimitRule LOGIN_FAILURE_RULE =
            new RateLimitRule(5, Duration.ofMinutes(15), Duration.ofMinutes(15));

    private final ConcurrentHashMap<String, CounterState> requestCounters = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, CounterState> loginFailures = new ConcurrentHashMap<>();

    @Override
    public RateLimitDecision tryConsume(String key, RateLimitRule rule) {
        return consume(requestCounters, key, rule, Instant.now());
    }

    @Override
    public void assertLoginIdentityAllowed(String email) {
        String key = loginFailureKey(email);
        CounterState state = loginFailures.get(key);
        Instant now = Instant.now();

        if (state != null && state.blockedUntil != null && now.isBefore(state.blockedUntil)) {
            long retryAfter = Math.max(1, Duration.between(now, state.blockedUntil).getSeconds());
            throw new RateLimitExceededException(
                    "Demasiados intentos. Intente nuevamente en unos minutos.",
                    retryAfter
            );
        }
    }

    @Override
    public void recordLoginFailure(String email) {
        consume(loginFailures, loginFailureKey(email), LOGIN_FAILURE_RULE, Instant.now());
    }

    @Override
    public void clearLoginFailures(String email) {
        loginFailures.remove(loginFailureKey(email));
    }

    private String loginFailureKey(String email) {
        return "login:id:" + email.trim().toLowerCase(Locale.ROOT);
    }

    private RateLimitDecision consume(
            ConcurrentHashMap<String, CounterState> store,
            String key,
            RateLimitRule rule,
            Instant now
    ) {
        AtomicReference<RateLimitDecision> decision = new AtomicReference<>(RateLimitDecision.permit());

        store.compute(key, (ignored, current) -> {
            if (current == null || now.isAfter(current.windowStart.plus(rule.window()))) {
                current = new CounterState(now);
            }

            if (current.blockedUntil != null) {
                if (now.isBefore(current.blockedUntil)) {
                    long retryAfter = Math.max(1, Duration.between(now, current.blockedUntil).getSeconds());
                    decision.set(RateLimitDecision.deny(retryAfter));
                    return current;
                }
                current = new CounterState(now);
            }

            current.count++;

            if (current.count > rule.maxRequests()) {
                current.blockedUntil = now.plus(rule.blockDuration());
                decision.set(RateLimitDecision.deny(rule.blockDuration().getSeconds()));
            }

            return current;
        });

        return decision.get();
    }

    private static final class CounterState {
        private int count;
        private final Instant windowStart;
        private Instant blockedUntil;

        private CounterState(Instant windowStart) {
            this.count = 0;
            this.windowStart = windowStart;
        }
    }
}


