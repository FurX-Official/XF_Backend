package com.xf.backend.module.user.entity;

import com.xf.backend.common.entity.BaseEntity;
import com.xf.backend.common.enums.AccountStatus;
import com.xf.backend.common.enums.Role;
import com.xf.backend.module.auth.entity.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_username", columnList = "username", unique = true),
        @Index(name = "idx_users_email", columnList = "email", unique = true)
})
public class User extends BaseEntity {

    @Column(name = "username", nullable = false, unique = true, length = 32)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Column(name = "display_name", nullable = false, length = 64)
    private String displayName;

    @Column(name = "bio", length = 500)
    private String bio;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    @Column(name = "age_verified", nullable = false)
    private boolean ageVerified = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private AccountStatus status = AccountStatus.ACTIVE;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UserRole> userRoles = new ArrayList<>();

    @Transient
    public Role getPrimaryRole() {
        return userRoles.stream()
                .map(UserRole::getRole)
                .max((a, b) -> b.ordinal() - a.ordinal())
                .orElse(Role.USER);
    }

    @Transient
    public Set<Role> getRoles() {
        Set<Role> roles = new HashSet<>();
        for (UserRole ur : userRoles) {
            roles.add(ur.getRole());
        }
        return roles;
    }
}
