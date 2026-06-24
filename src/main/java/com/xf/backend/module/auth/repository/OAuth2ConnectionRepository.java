package com.xf.backend.module.auth.repository;

import com.xf.backend.common.enums.OAuth2Provider;
import com.xf.backend.module.auth.entity.OAuth2Connection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OAuth2ConnectionRepository extends JpaRepository<OAuth2Connection, UUID> {

    Optional<OAuth2Connection> findByProviderAndProviderUserId(OAuth2Provider provider, String providerUserId);

    boolean existsByProviderAndProviderUserId(OAuth2Provider provider, String providerUserId);
}
