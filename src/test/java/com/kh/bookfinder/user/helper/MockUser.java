package com.kh.bookfinder.user.helper;

import com.kh.bookfinder.user.entity.User;
import com.kh.bookfinder.user.entity.UserRole;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class MockUser {

  public static User getMockUser() {
    return User.builder()
        .id(1L)
        .email("jinho4744@naver.com")
        .password(new BCryptPasswordEncoder().encode("test_password_123"))
        .nickname("고소하게")
        .role(UserRole.ROLE_ADMIN)
        .build();
  }
}
