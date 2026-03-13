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
        logger.info("CustomUserDetailsService: Searching for user with email: '{}'", email);
        return userRepository.findByEmail(email)
                .map(user -> {
                    logger.info("CustomUserDetailsService: User found in database: '{}'", user.getEmail());
                    String password = user.getPassword();
                    String passLog = (password != null && password.length() > 10) 
                            ? password.substring(0, 10) + "..." 
                            : password;
                    logger.debug("User found details: ID={}, Role={}, PassHashStart={}", 
                            user.getId(), user.getRole(), passLog);
                    return user;
                })
                .orElseThrow(() -> {
                    logger.error("CustomUserDetailsService: User NOT FOUND in database for email: '{}'", email);
                    return new UsernameNotFoundException("User not found: " + email);
                });
    }
}
