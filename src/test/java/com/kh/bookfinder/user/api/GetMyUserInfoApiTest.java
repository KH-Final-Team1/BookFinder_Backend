package com.kh.bookfinder.user.api;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kh.bookfinder.auth.login.dto.SecurityUserDetails;
import com.kh.bookfinder.book_trade.entity.BookTrade;
import com.kh.bookfinder.book_trade.helper.MockBookTrade;
import com.kh.bookfinder.book_trade.repository.BookTradeRepository;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.user.entity.User;
import com.kh.bookfinder.user.entity.UserRole;
import com.kh.bookfinder.user.helper.MockUser;
import com.kh.bookfinder.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
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

  @Test
  @DisplayName("관리자가 BookTrades를 갖고 있는 경우")
  public void getMyUserInfoSuccessOnLoginAdminWithBookTrades() throws Exception {
    // Given: 권한이 "ROLE_ADMIN"인 사용자가 주어진다.
    User mockUser = MockUser.getMockUser();
    mockUser.setRole(UserRole.ROLE_ADMIN);
    // And: mockUser는 mockBookTrades를 가지고 있다.
    ArrayList<BookTrade> mockBookTrades = MockBookTrade.getMockBookTradeListOnUser(mockUser, 10);
    // And: mockUser가 반환되도록 UserRepository를 Mocking
    // And: mockBookTrades가 반환되도록 BookTradesRepository를 Mocking
    when(userRepository.findByEmail(any()))
        .thenReturn(Optional.of(mockUser));
    when(bookTradeRepository.findByUserId(mockUser.getId()))
        .thenReturn(mockBookTrades);

    // When: User List API를 호출한다.
    ResultActions resultActions = callApiWithUser(mockUser);

    // Then: Status는 OK이다.
    resultActions.andExpect(status().isOk());
    // And: Response Body로 mockUser의 정보가 반환된다.
    assertMyUserInfo(resultActions, mockUser);
    // And: Response Body로 mockBookTrades의 정보가 반환된다.
    assertBookTrades(resultActions, mockBookTrades);
  }

  @Test
  @DisplayName("일반 사용자 로그인 된 상태에서 요청하는 경우")
  public void getMyUserInfoSuccessOnLoginUserOfRoleUser() throws Exception {
    // Given: 권한이 "ROLE_USER"인 사용자가 주어진다.
    User mockUser = MockUser.getMockUser();
    mockUser.setRole(UserRole.ROLE_USER);
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

  @Test
  @DisplayName("일반 사용자가 BookTrades를 갖고 있는 경우")
  public void getMyUserInfoSuccessOnLoginUserWithBookTrades() throws Exception {
    // Given: 권한이 "ROLE_USER"인 사용자가 주어진다.
    User mockUser = MockUser.getMockUser();
    mockUser.setRole(UserRole.ROLE_USER);
    // And: mockUser는 mockBookTrades를 가지고 있다.
    ArrayList<BookTrade> mockBookTrades = MockBookTrade.getMockBookTradeListOnUser(mockUser, 10);
    // And: mockUser가 반환되도록 UserRepository를 Mocking
    // And: mockBookTrades가 반환되도록 BookTradesRepository를 Mocking
    when(userRepository.findByEmail(any()))
        .thenReturn(Optional.of(mockUser));
    when(bookTradeRepository.findByUserId(mockUser.getId()))
        .thenReturn(mockBookTrades);

    // When: User List API를 호출한다.
    ResultActions resultActions = callApiWithUser(mockUser);

    // Then: Status는 OK이다.
    resultActions.andExpect(status().isOk());
    // And: Response Body로 mockUser의 정보가 반환된다.
    assertMyUserInfo(resultActions, mockUser);
    // And: Response Body로 mockBookTrades의 정보가 반환된다.
    assertBookTrades(resultActions, mockBookTrades);
  }

  @Test
  @DisplayName("이메일이 소셜 로그인에 해당하는 경우")
  public void getMyUserInfoSuccessOnLoginUserWithSocialLoginEmail() throws Exception {
    // Given: 권한이 "ROLE_USER"이고 이메일이 소셜로그인인 사용자가 주어진다.
    User mockUser = MockUser.getMockUser();
    mockUser.setRole(UserRole.ROLE_USER);
    mockUser.setEmail("testEmail@kakaoUser.com");

    // And: User, BookTrade Mocking
    when(userRepository.findByEmail(any()))
        .thenReturn(Optional.of(mockUser));

    // When: User List API를 호출한다.
    ResultActions resultActions = callApiWithUser(mockUser);

    // Then: Status는 OK이다.
    resultActions.andExpect(status().isOk());
    // And: Response Body의 email은 "카카오 로그인 사용자"이다.
    resultActions.andExpect(jsonPath("$.email", is("카카오 로그인 사용자")));
  }

  @Test
  @DisplayName("로그인 한 사용자 정보가 DB에 없는 경우")
  public void getMyUserInfoFailOnInvalidUserThatEmailNotExistsInDB() throws Exception {
    // Given: 사용자가 주어진다.
    User mockUser = MockUser.getMockUser();
    mockUser.setRole(UserRole.ROLE_USER);
    // And: UserRepository를 Mocking하지 않으므로 DB에는 mockUser가 없다.

    // When: User List API를 호출한다.
    ResultActions resultActions = callApiWithUser(mockUser);

    // Then: Status는 NOT FOUND이다.
    resultActions.andExpect(status().isNotFound());
    // And: Response Body로 message와 detail을 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(Message.NOT_FOUND)));
    resultActions.andExpect(jsonPath("$.detail", is(Message.NOT_FOUND_USER)));
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

  private void assertBookTrades(ResultActions resultActions, ArrayList<BookTrade> bookTrades) throws Exception {
    resultActions.andExpect(jsonPath("$.bookTrades", hasSize(bookTrades.size())));
    for (int i = 0; i < bookTrades.size(); i++) {
      String target = String.format("$.bookTrades[%d]", i);
      BookTrade bookTrade = bookTrades.get(i);
      resultActions.andExpect(jsonPath(target + ".id", is(bookTrade.getId().intValue())));
      resultActions.andExpect(jsonPath(target + ".tradeType", is(bookTrade.getTradeType().name())));
      resultActions.andExpect(jsonPath(target + ".tradeYn", is(bookTrade.getTradeYn().name())));
      resultActions.andExpect(jsonPath(target + ".rentalCost", is(bookTrade.getRentalCost())));
      resultActions.andExpect(jsonPath(target + ".createDate", is(bookTrade.getCreateDate().toString())));

      resultActions.andExpect(jsonPath(target + ".user.nickname", is(bookTrade.getUser().getNickname())));
      resultActions.andExpect(jsonPath(target + ".borough.name", is(bookTrade.getBorough().getName())));

      resultActions.andExpect(jsonPath(target + ".book.imageUrl", is(bookTrade.getBook().getImageUrl())));
      resultActions.andExpect(jsonPath(target + ".book.name", is(bookTrade.getBook().getName())));
      resultActions.andExpect(jsonPath(target + ".book.publicationYear", is(bookTrade.getBook().getPublicationYear())));
      resultActions.andExpect(jsonPath(target + ".book.authors", is(bookTrade.getBook().getAuthors())));
    }
  }
}
