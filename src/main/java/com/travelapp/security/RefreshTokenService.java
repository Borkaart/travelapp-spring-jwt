package com.travelapp.security;

import com.travelapp.entity.RefreshToken;
import com.travelapp.entity.User;
import com.travelapp.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    private static final long REFRESH_TOKEN_DAYS = 7;

    public RefreshToken create(User user) {
        // 1 refresh token por usuário (boa prática)
        repository.deleteByUser(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiresAt(Instant.now().plusSeconds(60 * 60 * 24 * REFRESH_TOKEN_DAYS))
                .revoked(false)
                .build();

        return repository.save(refreshToken);
    }
    @Transactional
    public RefreshToken validate(String token) {
        RefreshToken refreshToken = repository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (refreshToken.isRevoked()
                || refreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token expired or revoked");
        }

        return refreshToken;
    }

    public void revoke(RefreshToken token) {
        token.setRevoked(true);
        repository.save(token);
    }
}
