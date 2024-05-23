package com.kh.bookfinder.auth.oauth2.dto;

import java.util.Map;

public class GoogleOAuthAttributes extends OAuthAttributes {

  public GoogleOAuthAttributes(String nameAttributeKey, Map<String, Object> attributes) {
    super(nameAttributeKey, attributes);
  }

  @Override
  public String getId() {
    return String.valueOf(attributes.get("sub"));
  }

  @Override
  public String getProfileImageUrl() {
    return String.valueOf(attributes.get("picture"));
  }
}
