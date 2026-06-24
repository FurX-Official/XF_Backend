package com.xf.backend.module.auth.security;

import com.xf.backend.common.enums.AccountStatus;
import com.xf.backend.common.enums.OAuth2Provider;
import com.xf.backend.module.auth.entity.OAuth2Connection;
import com.xf.backend.module.user.entity.User;
import com.xf.backend.module.user.repository.UserRepository;
import com.xf.backend.module.auth.repository.OAuth2ConnectionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.UUID;

@Service
public class XFOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final Logger log = LoggerFactory.getLogger(XFOAuth2UserService.class);

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
    private final OAuth2ConnectionRepository oauth2ConnectionRepository;
    private final UserRepository userRepository;

    public XFOAuth2UserService(OAuth2ConnectionRepository oauth2ConnectionRepository,
                               UserRepository userRepository) {
        this.oauth2ConnectionRepository = oauth2ConnectionRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Provider provider = OAuth2Provider.valueOf(registrationId.toUpperCase());

        String providerUserId = oAuth2User.getName();
        String email = oAuth2User.getAttribute("email");
        String displayName = oAuth2User.getAttribute("name");

        return oauth2ConnectionRepository.findByProviderAndProviderUserId(provider, providerUserId)
                .map(connection -> {
                    User user = connection.getUser();
                    if (user.getStatus() != AccountStatus.ACTIVE) {
                        throw new OAuth2AuthenticationException("Account is " + user.getStatus().name().toLowerCase());
                    }
                    return buildOAuth2User(oAuth2User, connection.getUser());
                })
                .orElseGet(() -> {
                    User newUser = createNewUser(email, displayName);
                    OAuth2Connection connection = new OAuth2Connection();
                    connection.setProvider(provider);
                    connection.setProviderUserId(providerUserId);
                    connection.setEmail(email);
                    connection.setDisplayName(displayName);
                    connection.setUser(newUser);
                    oauth2ConnectionRepository.save(connection);
                    return buildOAuth2User(oAuth2User, newUser);
                });
    }

    private User createNewUser(String email, String displayName) {
        String username = generateUsername(email);
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setDisplayName(displayName != null ? displayName : username);
        user.setEmailVerified(true);
        user.setStatus(AccountStatus.ACTIVE);
        user.setPasswordHash(null);
        return userRepository.save(user);
    }

    private DefaultOAuth2User buildOAuth2User(OAuth2User oAuth2User, User user) {
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getPrimaryRole().name())),
                oAuth2User.getAttributes(),
                "name");
    }

    private String generateUsername(String email) {
        String base = email.split("@")[0].toLowerCase().replaceAll("[^a-z0-9]", "");
        if (userRepository.existsByUsername(base)) {
            return base + UUID.randomUUID().toString().substring(0, 6);
        }
        return base;
    }
}
