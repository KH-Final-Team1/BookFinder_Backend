package com.kh.bookfinder.user.helper;

import com.kh.bookfinder.book_trade.entity.Borough;
import com.kh.bookfinder.user.entity.User;
import com.kh.bookfinder.user.entity.UserRole;
import java.sql.Date;
import java.time.LocalDate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class MockUser {

  public static User getMockUser() {
    Borough borough = Borough.builder()
        .id(5L)
        .name("관악구")
        .build();
    return User.builder()
        .id(1L)
        .email("jinho4744@naver.com")
        .password(new BCryptPasswordEncoder().encode("test_password_123"))
        .nickname("고소하게")
        .phone("01012345678")
        .role(UserRole.ROLE_ADMIN)
        .address("서울 관악구 어딘가")
        .createDate(Date.valueOf(LocalDate.now()))
        .borough(borough)
        .build();
  }
}
