package com.example.be.user;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.be.entity.User;
import com.example.be.enums.Role;
import com.example.be.user.dto.UserResponse;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, UUID id);

    @Query(
            value = """
                    SELECT new com.example.be.user.dto.UserResponse(
                        u.id,
                        u.email,
                        u.role,
                        u.isActive,
                        u.createdAt,
                        u.updatedAt
                    )
                    FROM User u
                    WHERE (:role IS NULL OR u.role = :role)
                      AND (:active IS NULL OR u.isActive = :active)
                      AND (
                          :search IS NULL
                          OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
                      )
                    """,
            countQuery = """
                    SELECT COUNT(u)
                    FROM User u
                    WHERE (:role IS NULL OR u.role = :role)
                      AND (:active IS NULL OR u.isActive = :active)
                      AND (
                          :search IS NULL
                          OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
                      )
                    """
    )
    Page<UserResponse> findAllProjected(
            @Param("role") Role role,
            @Param("active") Boolean active,
            @Param("search") String search,
            Pageable pageable
    );
}
