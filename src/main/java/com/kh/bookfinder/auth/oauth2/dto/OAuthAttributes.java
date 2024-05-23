package com.kh.bookfinder.auth.oauth2.dto;

import com.kh.bookfinder.user.entity.User;
import com.kh.bookfinder.user.entity.UserRole;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;

@Getter
public abstract class OAuthAttributes {

  protected String nameAttributeKey;
  protected Map<String, Object> attributes;

  public OAuthAttributes(String nameAttributeKey, Map<String, Object> attributes) {
    this.nameAttributeKey = nameAttributeKey;
    this.attributes = attributes;
  }

  public static OAuthAttributes of(String socialType, String nameAttributeKey, Map<String, Object> attributes) {
    if (socialType.equals("kakao")) {
      return kakao(nameAttributeKey, attributes);
    }
    if (socialType.equals("google")) {
      return google(nameAttributeKey, attributes);
    }
    return null;
  }

  private static OAuthAttributes kakao(String nameAttributeKey, Map<String, Object> attributes) {
    return new KakaoOAuthAttributes(nameAttributeKey, attributes);
  }

  private static OAuthAttributes google(String nameAttributeKey, Map<String, Object> attributes) {
    return new GoogleOAuthAttributes(nameAttributeKey, attributes);
  }

  public abstract String getId();

  public abstract String getProfileImageUrl();

  public User toEntity(String socialType, OAuthAttributes attributes) {
    return User.builder()
        .email(UUID.randomUUID() + "@" + socialType + "User.com")
        .socialId(attributes.getId())
        .password("")
        .nickname(socialType + attributes.getId())
        .role(UserRole.ROLE_SOCIAL_GUEST)
        .build();
  }


}
