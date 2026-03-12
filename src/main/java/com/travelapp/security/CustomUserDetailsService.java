package com.travelapp.security;

import com.travelapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.debug("Loading user by email: {}", email);
        return userRepository.findByEmail(email)
                .map(user -> {
                    String password = user.getPassword();
                    String passLog = (password != null && password.length() > 10) 
                            ? password.substring(0, 10) + "..." 
                            : password;
                    logger.debug("User found: {}. Password hash start: {}", user.getEmail(), passLog);
                    return user;
                })
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", email);
                    return new UsernameNotFoundException("User not found");
                });
    }
}
