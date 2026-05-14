package com.example.be.auth;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody Map<String, String> body
    ) {

        String email = body.get("email");
        String password = body.get("password");

        authService.register(email, password);

        return ResponseEntity.ok("Register success");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> body,
            HttpServletResponse response
    ) {

        String email = body.get("email");
        String password = body.get("password");

        String token = authService.login(email, password);

        Cookie cookie = new Cookie("token", token);

        cookie.setHttpOnly(true);

        cookie.setPath("/");

        cookie.setMaxAge(60 * 60 * 24);

        response.addCookie(cookie);

        return ResponseEntity.ok(Map.of(
        "message", "Login success",
        "token", token
));
    }
}