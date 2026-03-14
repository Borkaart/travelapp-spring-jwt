package com.travelapp.controller;

import com.travelapp.dto.UserCreateRequest;
import org.springframework.security.access.prepost.PreAuthorize;
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
     * Lembrete meu: este endpoint e publico.
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
     * Lembrete meu: este endpoint exige JWT valido.
     * GET /api/users/me
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/me")
    public UserResponse me(
            @AuthenticationPrincipal User authUser
    ) {
        User user = userService.findById(authUser.getId());
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .isProfileCompleted(user.isProfileCompleted())
                .profileImage(user.getProfileImage())
                .build();
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String adminOnly() {
        return "Acesso ADMIN liberado";
    }

}
