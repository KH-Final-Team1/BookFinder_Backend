package com.kh.bookfinder.auth.oauth2.service;

import com.kh.bookfinder.auth.oauth2.dto.CustomOAuth2User;
import com.kh.bookfinder.auth.oauth2.dto.OAuthAttributes;
import com.kh.bookfinder.user.entity.User;
import com.kh.bookfinder.user.repository.UserRepository;
import java.util.Collections;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

  private final UserRepository userRepository;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
    OAuth2User oAuth2User = delegate.loadUser(userRequest);

    String registrationId = userRequest.getClientRegistration().getRegistrationId();
    String socialType = registrationId.toLowerCase();
    String userNameAttributeName = userRequest.getClientRegistration()
        .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
    Map<String, Object> defaultOAuthAttributes = oAuth2User.getAttributes();

    OAuthAttributes extractOAuthAttributes = OAuthAttributes
        .of(socialType, userNameAttributeName, defaultOAuthAttributes);

    assert extractOAuthAttributes != null;
    User createdServiceUser = userRepository.findBySocialId(extractOAuthAttributes.getId())
        .orElse(null);
    if (createdServiceUser == null) {
      createdServiceUser = extractOAuthAttributes.toEntity(socialType, extractOAuthAttributes);
      userRepository.save(createdServiceUser);
    }

    return new CustomOAuth2User(
        Collections.singleton(new SimpleGrantedAuthority(createdServiceUser.getRole().name())),
        defaultOAuthAttributes,
        extractOAuthAttributes.getNameAttributeKey(),
        createdServiceUser.getEmail(),
        createdServiceUser.getRole()
    );
  }
}
