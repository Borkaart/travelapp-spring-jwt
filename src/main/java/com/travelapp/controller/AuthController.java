package com.travelapp.controller;

import com.travelapp.dto.AuthResponse;
import com.travelapp.dto.RefreshRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.travelapp.dto.LoginRequest;
import com.travelapp.security.JwtService;
import com.travelapp.security.RefreshTokenService;
import com.travelapp.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody @Valid LoginRequest request) {
        logger.debug("Attempting login for user: {}", request.getEmail());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            User user = (User) authentication.getPrincipal();
            logger.debug("Login successful for user: {}", user.getEmail());

            String accessToken = jwtService.generateToken(user);
            String refreshToken = refreshTokenService.createForLogin(user);

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .expiresIn(jwtService.getJwtExpirationSeconds())
                    .build();
        } catch (AuthenticationException e) {
            logger.error("Login failed for user: {}. Reason: {}", request.getEmail(), e.getMessage());
            throw e;
        }
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody @Valid RefreshRequest request) {
        var result = refreshTokenService.rotate(request.refreshToken());
        String accessToken = jwtService.generateToken(result.user());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(result.refreshToken())
                .expiresIn(jwtService.getJwtExpirationSeconds())
                .build();
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestBody @Valid RefreshRequest request) {
        refreshTokenService.revoke(request.refreshToken());
    }
}
