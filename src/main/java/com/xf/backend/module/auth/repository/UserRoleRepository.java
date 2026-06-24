package com.xf.backend.module.auth.repository;

import com.xf.backend.module.auth.entity.UserRole;
import com.xf.backend.module.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    List<UserRole> findByUser(User user);
}
