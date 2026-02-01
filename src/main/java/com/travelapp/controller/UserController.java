package com.travelapp.controller;

import com.travelapp.dto.UserCreateRequest;
import com.travelapp.dto.UserResponse;
import com.travelapp.entity.User;
import com.travelapp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Endpoint público
     * POST /api/users
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(
            @RequestBody @Valid UserCreateRequest request
    ) {
        return userService.create(request);
    }

    /**
     * Endpoint protegido (JWT obrigatório)
     * GET /api/users/me
     */
    @GetMapping("/me")
    public UserResponse me(
            @AuthenticationPrincipal User user
    ) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
