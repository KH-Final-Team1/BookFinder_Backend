package com.kh.bookfinder.user.user_info;

import static com.kh.bookfinder.global.constants.HttpErrorMessage.UNAUTHORIZED;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kh.bookfinder.auth.jwt.service.JwtService;
import com.kh.bookfinder.auth.login.dto.SecurityUserDetails;
import com.kh.bookfinder.book_trade.entity.BookTrade;
import com.kh.bookfinder.book_trade.repository.BookTradeRepository;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.helper.MockBookTrade;
import com.kh.bookfinder.helper.MockUser;
import com.kh.bookfinder.user.entity.User;
import com.kh.bookfinder.user.entity.UserRole;
import com.kh.bookfinder.user.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.transaction.Transactional;
import java.util.List;
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

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
@Transactional
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class GetMyUserInfoApiTest {

  // 내 정보 가져오기 API
  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private JwtService jwtService;
  @MockBean
  private UserRepository userRepository;
  @MockBean
  private BookTradeRepository bookTradeRepository;

  private ResultActions callApiWithUser(User user) throws Exception {
    SecurityUserDetails securityUserDetails = new SecurityUserDetails(user);
    return mockMvc.perform(get("/api/v1/users/my-info")
        .with(user(securityUserDetails)));
  }

  private ResultActions callApiWithAuthorization(String accessToken) throws Exception {
    return mockMvc.perform(get("/api/v1/users/my-info").header("Authorization", "Bearer " + accessToken));
  }

  @Test
  @DisplayName("관리자 권한으로 요청하는 경우")
  public void success_OnAuthorization_WithRoleAdmin() throws Exception {
    // Given: 유효한 access token이 주어진다.
    String validAccessToken = "validAccessToken";
    // And: 권한이 "ROLE_ADMIN"인 사용자가 주어진다.
    User mockUser = MockUser.getMockUser();
    mockUser.setRole(UserRole.ROLE_ADMIN);

    // Mocking: JwtService가 validAccessToken를 반환
    when(jwtService.extractAccessToken(any())).thenReturn(validAccessToken);
    when(jwtService.validateToken(validAccessToken)).thenReturn(true);
    // And: JwtService가 mockUser의 email을 반환
    when(jwtService.extractEmail(validAccessToken)).thenReturn(mockUser.getEmail());
    // And: UserRepository가 mockUser를 반환
    when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));

    // When: User List API를 호출한다.
    ResultActions resultActions = callApiWithAuthorization(validAccessToken);

    // Then: Status는 OK이다.
    resultActions.andExpect(status().isOk());
    // And: Response Body로 mockUser의 정보가 반환된다.
    assertMyUserInfo(resultActions, mockUser);
    // And: Response Body로 Empty list의 bookTrades가 반환된다.
    resultActions.andExpect(jsonPath("$.bookTrades").isEmpty());
  }

  @Test
  @DisplayName("관리자 권한이고 BookTrades를 갖고 있는 경우")
  public void success_OnAuthorization_WithRoleAdmin_WithHavingBookTrades() throws Exception {
    // Given: 유효한 access token이 주어진다.
    String validAccessToken = "validAccessToken";
    // And: 권한이 "ROLE_ADMIN"인 사용자가 주어진다.
    User mockUser = MockUser.getMockUser();
    mockUser.setRole(UserRole.ROLE_ADMIN);
    // And: User가 mockUser인 BookTrades가 10개 주어진다.
    List<BookTrade> mockBookTrades = MockBookTrade.getMockBookTradeListOnUser(mockUser, 10);

    // Mocking: JwtService가 validAccessToken를 반환
    when(jwtService.extractAccessToken(any())).thenReturn(validAccessToken);
    when(jwtService.validateToken(validAccessToken)).thenReturn(true);
    // And: JwtService가 mockUser의 email을 반환
    when(jwtService.extractEmail(validAccessToken)).thenReturn(mockUser.getEmail());
    // Andg: UserRepository가 mockUser를 반환
    when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));
    // And: BookTradesRepository가 mockBookTrades를 반환
    when(bookTradeRepository.findByUserId(mockUser.getId())).thenReturn(mockBookTrades);

    // When: User List API를 호출한다.
    ResultActions resultActions = callApiWithAuthorization(validAccessToken);

    // Then: Status는 OK이다.
    resultActions.andExpect(status().isOk());
    // And: Response Body로 mockUser의 정보가 반환된다.
    assertMyUserInfo(resultActions, mockUser);
    // And: Response Body로 mockBookTrades의 정보가 반환된다.
    assertBookTrades(resultActions, mockBookTrades);
  }

  @Test
  @DisplayName("일반 사용자 권한으로 요청하는 경우")
  public void success_OnAuthorization_WithRoleUser() throws Exception {
    // Given: 유효한 access token이 주어진다.
    String validAccessToken = "validAccessToken";
    // And: 권한이 "ROLE_USER"인 사용자가 주어진다.
    User mockUser = MockUser.getMockUser();
    mockUser.setRole(UserRole.ROLE_USER);

    // Mocking: JwtService가 validAccessToken를 반환
    when(jwtService.extractAccessToken(any())).thenReturn(validAccessToken);
    when(jwtService.validateToken(validAccessToken)).thenReturn(true);
    // And: JwtService가 mockUser의 email을 반환
    when(jwtService.extractEmail(validAccessToken)).thenReturn(mockUser.getEmail());
    // And: UserRepository가 mockUser를 반환
    when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));

    // When: User List API를 호출한다.
    ResultActions resultActions = callApiWithAuthorization(validAccessToken);

    // Then: Status는 OK이다.
    resultActions.andExpect(status().isOk());
    // And: Response Body로 mockUser의 정보가 반환된다.
    assertMyUserInfo(resultActions, mockUser);
    // And: Response Body로 Empty list의 bookTrades가 반환된다.
    resultActions.andExpect(jsonPath("$.bookTrades").isEmpty());
  }

  @Test
  @DisplayName("일반 사용자 권한이고 BookTrades를 갖고 있는 경우")
  public void success_OnAuthorization_WithRoleUser_WithHavingBookTrades() throws Exception {
    // Given: 유효한 access token이 주어진다.
    String validAccessToken = "validAccessToken";
    // And: 권한이 "ROLE_USER"인 사용자가 주어진다.
    User mockUser = MockUser.getMockUser();
    mockUser.setRole(UserRole.ROLE_USER);
    // And: User가 mockUser인 BookTrades가 10개 주어진다.
    List<BookTrade> mockBookTrades = MockBookTrade.getMockBookTradeListOnUser(mockUser, 10);

    // Mocking: JwtService가 validAccessToken를 반환
    when(jwtService.extractAccessToken(any())).thenReturn(validAccessToken);
    when(jwtService.validateToken(validAccessToken)).thenReturn(true);
    // And: JwtService가 mockUser의 email을 반환
    when(jwtService.extractEmail(validAccessToken)).thenReturn(mockUser.getEmail());
    // Andg: UserRepository가 mockUser를 반환
    when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));
    // And: BookTradesRepository가 mockBookTrades를 반환
    when(bookTradeRepository.findByUserId(mockUser.getId())).thenReturn(mockBookTrades);

    // When: User List API를 호출한다.
    ResultActions resultActions = callApiWithAuthorization(validAccessToken);

    // Then: Status는 OK이다.
    resultActions.andExpect(status().isOk());
    // And: Response Body로 mockUser의 정보가 반환된다.
    assertMyUserInfo(resultActions, mockUser);
    // And: Response Body로 mockBookTrades의 정보가 반환된다.
    assertBookTrades(resultActions, mockBookTrades);
  }

  @Test
  @DisplayName("일반 사용자 권한이고 소셜 로그인에 해당하는 경우")
  public void success_OnAuthorization_WithRoleUser_AndSocialLoginUser() throws Exception {
    // Given: 유효한 access token이 주어진다.
    String validAccessToken = "validAccessToken";
    // And: 권한이 "ROLE_USER"이고 이메일이 카카오 로그인인 사용자가 주어진다.
    User mockUser = MockUser.getMockUser();
    mockUser.setRole(UserRole.ROLE_USER);
    mockUser.setEmail("test@kakaoUser.com");

    // Mocking: JwtService가 validAccessToken를 반환
    when(jwtService.extractAccessToken(any())).thenReturn(validAccessToken);
    when(jwtService.validateToken(validAccessToken)).thenReturn(true);
    // And: JwtService가 mockUser의 email을 반환
    when(jwtService.extractEmail(validAccessToken)).thenReturn(mockUser.getEmail());
    // And: UserRepository가 mockUser를 반환
    when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));

    // When: User List API를 호출한다.
    ResultActions resultActions = callApiWithAuthorization(validAccessToken);

    // Then: Status는 OK이다.
    resultActions.andExpect(status().isOk());
    // And: Response Body의 email은 "카카오 로그인 사용자"이다.
    resultActions.andExpect(jsonPath("$.email", is("카카오 로그인 사용자")));
  }

  @Test
  @DisplayName("권한 없이 요청하는 경우")
  public void fail_OnNoAuthorization() throws Exception {
    // Mocking: JwtService가 null를 반환
    when(jwtService.extractAccessToken(any())).thenReturn(null);
    when(jwtService.validateToken(null)).thenThrow(new JwtException(Message.NOT_LOGIN));

    // When: User List API를 호출한다.
    ResultActions resultActions = mockMvc.perform(get("/api/v1/users/my-info"));

    // Then: Status는 UNAUTHORIZED이다.
    resultActions.andExpect(status().isUnauthorized());
    // And: Response Body로 message와 detail을 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(UNAUTHORIZED.getMessage())));
    resultActions.andExpect(jsonPath("$.detail", is(Message.NOT_LOGIN)));
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

  private void assertBookTrades(ResultActions resultActions, List<BookTrade> bookTrades) throws Exception {
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
