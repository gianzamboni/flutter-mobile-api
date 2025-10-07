package com.pines.flutter.capacitacion.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        final Instant start = Instant.now();
        final String method = request.getMethod();
        final String uri = request.getRequestURI();
        final String query = request.getQueryString();
        final String remoteAddr = request.getRemoteAddr();

        try {
            filterChain.doFilter(request, response);
        } finally {
            int status = response.getStatus();
            long ms = Duration.between(start, Instant.now()).toMillis();
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String user = (auth != null && auth.isAuthenticated()) ? auth.getName() : "anonymous";
            if (query != null && !query.isEmpty()) {
                log.info("{} {}?{} -> {} ({} ms) user={} ip={}", method, uri, query, status, ms, user, remoteAddr);
            } else {
                log.info("{} {} -> {} ({} ms) user={} ip={}", method, uri, status, ms, user, remoteAddr);
            }
        }
    }
}


