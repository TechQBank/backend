package com.qbank.auth.oauth2;

import com.qbank.user.domain.User;
import com.qbank.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(request);
        String provider = request.getClientRegistration().getRegistrationId().toUpperCase();

        String providerId = extractProviderId(provider, oAuth2User);
        String email = oAuth2User.getAttribute("email");
        String name = extractName(provider, oAuth2User);

        User user = userRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> userRepository.save(User.createOAuth(provider, providerId, email, name)));

        return new CustomOAuth2User(user, oAuth2User.getAttributes());
    }

    private String extractProviderId(String provider, OAuth2User user) {
        // GitHub는 id가 Integer로 반환됨
        Object id = user.getAttribute("id");
        return id != null ? String.valueOf(id) : user.getAttribute("login");
    }

    private String extractName(String provider, OAuth2User user) {
        String name = user.getAttribute("name");
        if (name != null && !name.isBlank()) return name;
        // GitHub는 name이 null일 수 있으므로 login(username) 사용
        return user.getAttribute("login");
    }
}
