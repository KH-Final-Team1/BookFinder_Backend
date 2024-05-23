package com.kh.bookfinder.auth.oauth2.dto;

import java.util.Map;

public class KakaoOAuthAttributes extends OAuthAttributes {

  public KakaoOAuthAttributes(String nameAttributeKey, Map<String, Object> attributes) {
    super(nameAttributeKey, attributes);
  }

  @Override
  public String getId() {
    return String.valueOf(attributes.get("id"));
  }

  @Override
  @SuppressWarnings("unchecked")
  public String getProfileImageUrl() {
    Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");

    if (account == null) {
      return null;
    }

    Map<String, Object> profile = (Map<String, Object>) account.get("profile");

    if (profile == null) {
      return null;
    }

    return (String) profile.get("thumbnail_image_url");
  }
}
