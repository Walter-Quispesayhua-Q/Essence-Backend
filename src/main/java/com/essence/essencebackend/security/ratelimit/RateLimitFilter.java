package com.essence.essencebackend.security.ratelimit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private static final RateLimitRule LOGIN_RULE =
            new RateLimitRule(10, Duration.ofMinutes(1), Duration.ofMinutes(5));

    private static final RateLimitRule REGISTER_RULE =
            new RateLimitRule(5, Duration.ofMinutes(15), Duration.ofMinutes(15));

    private static final RateLimitRule USERNAME_RULE =
            new RateLimitRule(30, Duration.ofMinutes(1), Duration.ofMinutes(2));

    private static final RateLimitRule SEARCH_RULE =
            new RateLimitRule(30, Duration.ofMinutes(1), Duration.ofMinutes(2));

    private static final RateLimitRule HOME_RULE =
            new RateLimitRule(60, Duration.ofMinutes(1), Duration.ofMinutes(1));

    private final RateLimitService rateLimitService;
    private final ClientIpResolver clientIpResolver;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        RateLimitRule rule = resolveRule(request);
        if (rule == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String ip = clientIpResolver.resolve(request);
        String key = buildKey(request, ip);

        RateLimitDecision decision = rateLimitService.tryConsume(key, rule);
        if (!decision.isAllowed()) {
            writeTooManyRequests(response, decision.retryAfterSeconds());
            return;
        }

        filterChain.doFilter(request, response);
    }

    private RateLimitRule resolveRule(HttpServletRequest request) {
        String method = request.getMethod();
        String path = request.getServletPath();

        if ("POST".equals(method) && "/api/v1/login".equals(path)) {
            return LOGIN_RULE;
        }
        if ("POST".equals(method) && "/api/v1/register".equals(path)) {
            return REGISTER_RULE;
        }
        if ("GET".equals(method) && "/api/v1/register/username".equals(path)) {
            return USERNAME_RULE;
        }
        if ("GET".equals(method) && "/api/v1/search".equals(path)) {
            return SEARCH_RULE;
        }
        if ("GET".equals(method) && "/api/v1/home".equals(path)) {
            return HOME_RULE;
        }

        return null;
    }

    private String buildKey(HttpServletRequest request, String ip) {
        String method = request.getMethod();
        String path = request.getServletPath();

        if ("POST".equals(method) && "/api/v1/login".equals(path)) {
            return "login:ip:" + ip;
        }
        if ("POST".equals(method) && "/api/v1/register".equals(path)) {
            return "register:ip:" + ip;
        }
        if ("GET".equals(method) && "/api/v1/register/username".equals(path)) {
            return "register-username:ip:" + ip;
        }
        if ("GET".equals(method) && "/api/v1/search".equals(path)) {
            return "search:ip:" + ip;
        }
        if ("GET".equals(method) && "/api/v1/home".equals(path)) {
            return "home:ip:" + ip;
        }

        return "public:ip:" + ip;
    }

    private void writeTooManyRequests(HttpServletResponse response, long retryAfterSeconds) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader(HttpHeaders.RETRY_AFTER, String.valueOf(retryAfterSeconds));

        String body = """
                {
                  "title": "Demasiadas solicitudes",
                  "detail": "Intente nuevamente en unos minutos.",
                  "status": 429
                }
                """;

        response.getWriter().write(body);
    }
}
