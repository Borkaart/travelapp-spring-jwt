package com.travelapp.security;

import com.travelapp.entity.RefreshToken;
import com.travelapp.entity.User;
import com.travelapp.exception.InvalidRefreshTokenException;
import com.travelapp.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-expiration-days:7}")
    private long refreshExpirationDays;

    @Transactional
    public RefreshToken createForLogin(User user) {
        // Quando o usuario loga, eu removo os tokens antigos para manter o fluxo simples e seguro.
        refreshTokenRepository.deleteByUser(user);

        return refreshTokenRepository.save(buildToken(user));
    }

    @Transactional
    public RefreshToken rotate(String oldToken) {
        RefreshToken existing = refreshTokenRepository.findByToken(oldToken)
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
        RefreshToken newToken = buildToken(existing.getUser());
        return refreshTokenRepository.save(newToken);
    }

    @Transactional
    public void revoke(String token) {
        RefreshToken existing = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token not found"));

        existing.setRevoked(true);
        refreshTokenRepository.save(existing);
    }

    private RefreshToken buildToken(User user) {
        return RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .revoked(false)
                .expiresAt(Instant.now().plus(refreshExpirationDays, ChronoUnit.DAYS))
                .build();
    }
}
