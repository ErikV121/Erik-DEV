package com.erikv121.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RateLimitingFilter implements Filter {

    private static final int MAX_REQUESTS_PER_MINUTE = 10;
    private static final long WINDOW_MILLIS = Duration.ofMinutes(1).toMillis();

    private static final Set<String> LIMITED_PATHS = Set.of("/", "/sendMail", "/other");

    private static class WindowCounter {
        volatile long windowStartMillis;
        final AtomicInteger count = new AtomicInteger(0);

        WindowCounter(long windowStartMillis) {
            this.windowStartMillis = windowStartMillis;
        }
    }

    private final ConcurrentHashMap<String, WindowCounter> requestCounts = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getRequestURI();

        if (!LIMITED_PATHS.contains(path)) {
            chain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIp(req);
        long now = System.currentTimeMillis();

        WindowCounter counter = requestCounts.compute(clientIp, (ip, existing) -> {
            if (existing == null) return new WindowCounter(now);

            if (now - existing.windowStartMillis >= WINDOW_MILLIS) {
                existing.windowStartMillis = now;
                existing.count.set(0);
            }
            return existing;
        });

        int currentCount = counter.count.incrementAndGet();

        if (currentCount > MAX_REQUESTS_PER_MINUTE) {
            long retryAfterSeconds = Math.max(1, (WINDOW_MILLIS - (now - counter.windowStartMillis)) / 1000);

            res.setStatus(429);
            res.setHeader("Retry-After", String.valueOf(retryAfterSeconds));
            res.getWriter().write("Too many requests. Please try again later.");
            return;
        }

        chain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest req) {
        String xff = req.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return req.getRemoteAddr();
    }
}
