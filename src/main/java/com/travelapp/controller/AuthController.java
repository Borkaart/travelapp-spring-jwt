package com.travelapp.controller;

import com.travelapp.dto.AuthResponse;
import com.travelapp.dto.LoginRequest;
import com.travelapp.entity.RefreshToken;
import com.travelapp.entity.User;
import com.travelapp.security.JwtService;
import com.travelapp.security.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService; // 👈 AQUI

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );

        User user = (User) authentication.getPrincipal();

        String accessToken = jwtService.generateToken(user);
        String refreshToken = refreshTokenService.create(user).getToken();

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestParam String refreshToken) {

        RefreshToken token = refreshTokenService.validate(refreshToken);

        String newAccessToken =
                jwtService.generateToken(token.getUser());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @PostMapping("/logout")
    public void logout(@RequestParam String refreshToken) {
        RefreshToken token = refreshTokenService.validate(refreshToken);
        refreshTokenService.revoke(token);
    }
}
