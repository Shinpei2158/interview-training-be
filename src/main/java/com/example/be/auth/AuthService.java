package com.example.be.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.be.auth.jwt.JwtService;
import com.example.be.entity.User;
import com.example.be.enums.Role;
import com.example.be.exception.UnauthorizedException;
import com.example.be.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public User register(String email, String password) {
        boolean existed = userRepository.existsByEmail(email);

        if (existed) {
            throw new UnauthorizedException("Email already exists");
        }

        String hashedPassword = passwordEncoder.encode(password);

        User user = User.builder()
                .email(email)
                .password(hashedPassword)
                .role(Role.USER)
                .build();

        return userRepository.save(user);
    }


    public String login(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UnauthorizedException("Email or password is incorrect"));

        boolean matched = passwordEncoder.matches(
                password,
                user.getPassword()
        );

        if (!matched) {
            throw new UnauthorizedException("Email or password is incorrect");
        }

        return jwtService.generateToken(user);
    }
}