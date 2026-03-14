package com.travelapp.social.api;

import com.travelapp.entity.User;
import com.travelapp.social.dto.ProfileImageUpdateDto;
import com.travelapp.social.dto.UpdateProfileDto;
import com.travelapp.social.dto.UserProfileDto;
import com.travelapp.social.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getMyProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userProfileService.getProfile(user.getId()));
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(userProfileService.getProfile(userId));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileDto> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody UpdateProfileDto dto) {
        return ResponseEntity.ok(userProfileService.updateProfile(user.getId(), dto));
    }
    
    @PutMapping("/profile/image")
    public ResponseEntity<UserProfileDto> updateProfileImage(
            @AuthenticationPrincipal User user,
            @RequestBody ProfileImageUpdateDto dto) {
        return ResponseEntity.ok(userProfileService.updateProfileImage(user.getId(), dto.getImageUrl()));
    }
}
