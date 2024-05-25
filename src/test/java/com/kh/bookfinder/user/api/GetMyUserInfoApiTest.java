package com.kh.bookfinder.user.api;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kh.bookfinder.auth.login.dto.SecurityUserDetails;
import com.kh.bookfinder.book_trade.repository.BookTradeRepository;
import com.kh.bookfinder.user.entity.User;
import com.kh.bookfinder.user.entity.UserRole;
import com.kh.bookfinder.user.helper.MockUser;
import com.kh.bookfinder.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
@Transactional
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class GetMyUserInfoApiTest {

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private UserRepository userRepository;
  @MockBean
  private BookTradeRepository bookTradeRepository;

  @Test
  @DisplayName("관리자 로그인 된 상태에서 요청하는 경우")
  public void getMyUserInfoSuccessOnLoginUserOfRoleAdmin() throws Exception {
    // Given: 권한이 "ROLE_ADMIN"인 사용자가 주어진다.
    User mockUser = MockUser.getMockUser();
    mockUser.setRole(UserRole.ROLE_ADMIN);
    // And: mockUser가 반환되도록 UserRepository를 Mocking
    when(userRepository.findByEmail(any()))
        .thenReturn(Optional.of(mockUser));

    // When: User List API를 호출한다.
    ResultActions resultActions = callApiWithUser(mockUser);

    // Then: Status는 OK이다.
    resultActions.andExpect(status().isOk());
    // And: Response Body로 mockUser의 정보가 반환된다.
    assertMyUserInfo(resultActions, mockUser);
    // And: Response Body로 Empty list의 bookTrades가 반환된다.
    resultActions.andExpect(jsonPath("$.bookTrades").isEmpty());
  }


  private ResultActions callApiWithUser(User user) throws Exception {
    SecurityUserDetails securityUserDetails = new SecurityUserDetails(user);
    return mockMvc.perform(MockMvcRequestBuilders
        .get("/api/v1/users/my-info")
        .with(user(securityUserDetails)));
  }

  private void assertMyUserInfo(ResultActions resultActions, User mockUser) throws Exception {
    resultActions.andExpect(jsonPath("$.id", is(mockUser.getId().intValue())));
    resultActions.andExpect(jsonPath("$.email", is(mockUser.getEmail())));
    resultActions.andExpect(jsonPath("$.phone", is(mockUser.getPhone())));
    resultActions.andExpect(jsonPath("$.nickname", is(mockUser.getNickname())));
    resultActions.andExpect(jsonPath("$.borough", is(mockUser.extractBoroughName())));
    resultActions.andExpect(jsonPath("$.role", is(mockUser.getRole().name())));
    resultActions.andExpect(jsonPath("$.createDate", is(mockUser.getCreateDate().toString())));
  }

}
