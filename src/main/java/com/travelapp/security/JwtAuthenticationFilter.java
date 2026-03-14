package com.travelapp.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        String method = request.getMethod();

        boolean shouldSkip = HttpMethod.OPTIONS.matches(method)
                || path.startsWith("/api/auth")
                || path.equals("/api/health")
                || path.equals("/")
                || (path.equals("/api/users") && HttpMethod.POST.matches(method))
                || path.equals("/error")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.equals("/swagger-ui.html");
        
        if (shouldSkip) {
            logger.debug("Skipping JWT filter for path: {}", path);
        }
        return shouldSkip;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // Se nao vier Bearer, eu deixo seguir sem autenticar e o SecurityChain decide 401/403 depois.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.debug("No Bearer token found in request to {}", request.getServletPath());
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7).trim();

        try {
            final String username = jwtService.extractUsername(jwt);
            logger.debug("JWT extracted username: {}", username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.debug("User authenticated via JWT: {}", username);
                } else {
                    logger.warn("Invalid token for user: {}", username);
                    writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired JWT");
                    return;

                }
            }

        } catch (JwtException | IllegalArgumentException e) {
            logger.error("JWT Error: {}", e.getMessage());
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void writeError(HttpServletResponse response, int status, String message) throws IOException {
        if (response.isCommitted()) return;

        response.resetBuffer();
        response.setStatus(status);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        String reason = status == 401 ? "Unauthorized" : "Error";

        Map<String, Object> body = Map.of(
                "timestamp", Instant.now(),
                "status", status,
                "error", reason,
                "message", message
        );

        response.getWriter().write(toJson(body));
        response.flushBuffer();
    }

    private String toJson(Map<String, Object> body) {
        String timestamp = String.valueOf(body.get("timestamp"));
        String status = String.valueOf(body.get("status"));
        String error = String.valueOf(body.get("error"));
        String message = String.valueOf(body.get("message"));

        return """
                {
                  "timestamp": "%s",
                  "status": %s,
                  "error": "%s",
                  "message": "%s"
                }
                """.formatted(escapeJson(timestamp), status, escapeJson(error), escapeJson(message));
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
