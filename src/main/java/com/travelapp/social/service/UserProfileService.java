package com.travelapp.social.service;

import com.travelapp.entity.User;
import com.travelapp.repository.UserRepository;
import com.travelapp.social.dto.UpdateProfileDto;
import com.travelapp.social.dto.UserProfileDto;
import com.travelapp.social.dto.UserSummaryDto;
import com.travelapp.social.entity.UserProfile;
import com.travelapp.social.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserProfileDto getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile profile = userProfileRepository.findByUser(user)
                .orElse(UserProfile.builder().user(user).build());

        return mapToDto(profile);
    }

    @Transactional
    public UserProfileDto updateProfile(Long userId, UpdateProfileDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile profile = userProfileRepository.findByUser(user)
                .orElse(UserProfile.builder().user(user).build());

        // Update User info
        if (dto.getName() != null) {
            user.setName(dto.getName());
            userRepository.save(user);
        }

        // Update Profile info
        profile.setBio(dto.getBio());
        profile.setPhoneNumber(dto.getPhoneNumber());
        profile.setBirthDate(dto.getBirthDate());
        profile.setCity(dto.getCity());
        profile.setCountry(dto.getCountry());
        profile.setInstagramLink(dto.getInstagramLink());
        profile.setFacebookLink(dto.getFacebookLink());
        profile.setWebsiteLink(dto.getWebsiteLink());

        UserProfile savedProfile = userProfileRepository.save(profile);
        return mapToDto(savedProfile);
    }
    
    @Transactional
    public UserProfileDto updateProfileImage(Long userId, String imageUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setProfileImage(imageUrl);
        userRepository.save(user);
        
        UserProfile profile = userProfileRepository.findByUser(user)
                .orElse(UserProfile.builder().user(user).build());
                
        return mapToDto(profile);
    }

    private UserProfileDto mapToDto(UserProfile profile) {
        User user = profile.getUser();
        return UserProfileDto.builder()
                .id(profile.getId())
                .user(UserSummaryDto.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .profileImage(user.getProfileImage())
                        .build())
                .bio(profile.getBio())
                .phoneNumber(profile.getPhoneNumber())
                .birthDate(profile.getBirthDate())
                .city(profile.getCity())
                .country(profile.getCountry())
                .instagramLink(profile.getInstagramLink())
                .facebookLink(profile.getFacebookLink())
                .websiteLink(profile.getWebsiteLink())
                .build();
    }
}
