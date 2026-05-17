package com.example.be.user;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.be.entity.User;
import com.example.be.enums.Role;
import com.example.be.exception.GlobalException;
import com.example.be.user.dto.UserCreateRequest;
import com.example.be.user.dto.UserResponse;
import com.example.be.user.dto.UserUpdateRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final int MAX_PAGE_SIZE = 100;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<UserResponse> findAll(
            Role role,
            Boolean active,
            String search,
            Pageable pageable
    ) {
        Pageable safePageable = capPageSize(pageable);
        String normalizedSearch = normalizeSearch(search);

        return userRepository.findAllProjected(
                role,
                active,
                normalizedSearch,
                safePageable
        );
    }

    @Transactional(readOnly = true)
    public UserResponse findById(UUID id) {
        User user = getUser(id);
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse create(UserCreateRequest request) {
        validateUniqueEmail(request.getEmail(), null);

        User user = User.builder()
                .email(request.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() == null ? Role.USER : request.getRole())
                .isActive(request.getActive() == null || request.getActive())
                .build();

        User saved = userRepository.save(user);
        return UserResponse.from(saved);
    }

    @Transactional
    public UserResponse update(UUID id, UserUpdateRequest request) {
        User user = getUser(id);

        validateUniqueEmail(request.getEmail(), id);

        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setRole(request.getRole());
        user.setActive(request.getActive());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return UserResponse.from(user);
    }

    @Transactional
    public void delete(UUID id) {
        User user = getUser(id);
        user.setActive(false);
    }

    private User getUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new GlobalException("NOT_FOUND", "User not found"));
    }

    private void validateUniqueEmail(String email, UUID excludeId) {
        String normalizedEmail = email.trim().toLowerCase();
        boolean exists = excludeId == null
                ? userRepository.existsByEmailIgnoreCase(normalizedEmail)
                : userRepository.existsByEmailIgnoreCaseAndIdNot(
                        normalizedEmail,
                        excludeId
                );

        if (exists) {
            throw new GlobalException("BAD_REQUEST", "Email already exists");
        }
    }

    private Pageable capPageSize(Pageable pageable) {
        int size = Math.min(pageable.getPageSize(), MAX_PAGE_SIZE);
        return PageRequest.of(
                pageable.getPageNumber(),
                size,
                pageable.getSort()
        );
    }

    private String normalizeSearch(String search) {
        if (search == null) {
            return null;
        }

        String trimmed = search.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
