package com.travelapp.security;

import com.travelapp.entity.RefreshToken;
import com.travelapp.entity.User;
import com.travelapp.exception.InvalidRefreshTokenException;
import com.travelapp.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-expiration-days:7}")
    private long refreshExpirationDays;

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(RefreshTokenService.class);

    @Transactional
    public String createForLogin(User user) {
        try {
            logger.info("Creating refresh token for login - User: {}", user.getEmail());
            // Quando o usuario loga, eu removo os tokens antigos para manter o fluxo simples e seguro.
            refreshTokenRepository.deleteByUser(user);
            logger.info("Old refresh tokens deleted for user: {}", user.getEmail());

            String rawToken = newRawToken();
            refreshTokenRepository.save(buildToken(user, rawToken));
            logger.info("New refresh token saved successfully for user: {}", user.getEmail());
            return rawToken;
        } catch (Exception e) {
            logger.error("Error creating refresh token: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public RotationResult rotate(String oldToken) {
        RefreshToken existing = refreshTokenRepository.findByTokenHash(hash(oldToken))
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token not found"));

        if (existing.isRevoked()) {
            throw new InvalidRefreshTokenException("Refresh token revoked");
        }

        if (existing.getExpiresAt().isBefore(Instant.now())) {
            throw new InvalidRefreshTokenException("Refresh token expired");
        }

        // Aqui eu revogo o token antigo para fazer a rotacao.
        existing.setRevoked(true);
        refreshTokenRepository.save(existing);

        // Depois eu emito um novo refresh token para o mesmo usuario.
        String newRawToken = newRawToken();
        RefreshToken newToken = buildToken(existing.getUser(), newRawToken);
        refreshTokenRepository.save(newToken);
        return new RotationResult(existing.getUser(), newRawToken);
    }

    @Transactional
    public void revoke(String token) {
        RefreshToken existing = refreshTokenRepository.findByTokenHash(hash(token))
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token not found"));

        existing.setRevoked(true);
        refreshTokenRepository.save(existing);
    }

    public record RotationResult(User user, String refreshToken) {}

    private RefreshToken buildToken(User user, String rawToken) {
        return RefreshToken.builder()
                .tokenHash(hash(rawToken))
                .user(user)
                .revoked(false)
                .expiresAt(Instant.now().plus(refreshExpirationDays, ChronoUnit.DAYS))
                .build();
    }

    private String newRawToken() {
        return UUID.randomUUID().toString();
    }

    private String hash(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new InvalidRefreshTokenException("Refresh token invalid");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(rawToken.trim().getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return toHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    private String toHex(byte[] bytes) {
        char[] out = new char[bytes.length * 2];
        final char[] hex = "0123456789abcdef".toCharArray();
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            out[i * 2] = hex[v >>> 4];
            out[i * 2 + 1] = hex[v & 0x0F];
        }
        return new String(out);
    }
}
