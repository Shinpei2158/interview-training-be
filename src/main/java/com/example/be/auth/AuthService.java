package com.example.be.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.be.auth.jwt.JwtService;
import com.example.be.entity.User;
import com.example.be.enums.Role;
import com.example.be.exception.GlobalException;
import com.example.be.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public User register(String email, String password) {
        String normalizedEmail = email.trim().toLowerCase();
        boolean existed = userRepository.existsByEmailIgnoreCase(normalizedEmail);

        if (existed) {
            throw GlobalException.unauthorized("Email already exists");
        }

        String hashedPassword = passwordEncoder.encode(password);

        User user = User.builder()
                .email(normalizedEmail)
                .password(hashedPassword)
                .role(Role.USER)
                .isActive(true)
                .build();

        return userRepository.save(user);
    }

    public String login(String email, String password) {
        User user = userRepository.findByEmailIgnoreCase(email.trim().toLowerCase())
                .orElseThrow(() ->
                        GlobalException.unauthorized("Email or password is incorrect"));

        if (!user.isActive()) {
            throw GlobalException.unauthorized("Email or password is incorrect");
        }

        boolean matched = passwordEncoder.matches(
                password,
                user.getPassword()
        );

        if (!matched) {
            throw GlobalException.unauthorized("Email or password is incorrect");
        }

        return jwtService.generateToken(user);
    }
}