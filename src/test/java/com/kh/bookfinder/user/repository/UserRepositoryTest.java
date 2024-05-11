package com.kh.bookfinder.user.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.kh.bookfinder.user.entity.User;
import com.kh.bookfinder.user.entity.UserRole;
import com.kh.bookfinder.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@ActiveProfiles("test")
@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UserRepositoryTest {

  @Autowired
  UserRepository userRepository;
  @Autowired
  EntityManager entityManager;

  @Test
  public void userInsertTest1() {
    // Given: User가 주어진다.
    User givenUser = User.builder()
        .email("user@kh.kr")
        .password("password")
        .phone("010-1234-5678")
        .nickname("nickname")
        .role(UserRole.ROLE_ADMIN)
        .address("서울시 테헤란로 10길")
        .build();

    // When: save() 메서드를 호출하여 User를 저장한다.
    User actual = this.userRepository.save(givenUser);

    // Then: actual은 givenUser와 같다.
    assertThat(actual).isNotNull();
    assertThat(actual).isEqualTo(givenUser);
  }

  @Test
  @DisplayName("email은 unique로 중복이 불가능하다")
  public void userInsertTest2() {
    // Given: email이 같은 User가 2개 주어진다.
    User givenUser1 = User.builder()
        .email("user@kh.kr")
        .password("password")
        .phone("010-1234-5678")
        .nickname("nickname")
        .role(UserRole.ROLE_ADMIN)
        .address("서울시 테헤란로 10길")
        .build();
    User givenUser2 = User.builder()
        .email("user@kh.kr")
        .password("password2")
        .phone("010-5678-1234")
        .nickname("nickname2")
        .role(UserRole.ROLE_ADMIN)
        .address("서울시 테헤란로 10길")
        .build();

    // When: save() 메서드를 호출하여 givenUser1를 저장한다.
    User actual = this.userRepository.save(givenUser1);
    // And: save() 메서드를 호출하여 givenUser2를 저장한다.
    // Then: Exception이 발생한다.
    assertThatThrownBy(() -> this.userRepository.save(givenUser2))
        .isInstanceOf(DataIntegrityViolationException.class)
        .hasMessageContaining("Duplicate");
  }

  @Test
  @DisplayName("nickname은 unique로 중복이 불가능하다")
  public void userInsertTest3() {
    // Given: email이 같은 User가 2개 주어진다.
    User givenUser1 = User.builder()
        .email("user@kh.kr")
        .password("password")
        .phone("010-1234-5678")
        .nickname("nickname")
        .role(UserRole.ROLE_ADMIN)
        .address("서울시 테헤란로 10길")
        .build();
    User givenUser2 = User.builder()
        .email("user2@kh.kr")
        .password("password2")
        .phone("010-5678-1234")
        .nickname("nickname")
        .role(UserRole.ROLE_ADMIN)
        .address("서울시 테헤란로 10길")
        .build();

    // When: save() 메서드를 호출하여 givenUser1를 저장한다.
    User actual = this.userRepository.save(givenUser1);
    // And: save() 메서드를 호출하여 givenUser2를 저장한다.
    // Then: Exception이 발생한다.
    assertThatThrownBy(() -> this.userRepository.save(givenUser2))
        .isInstanceOf(DataIntegrityViolationException.class)
        .hasMessageContaining("Duplicate");
  }
}
