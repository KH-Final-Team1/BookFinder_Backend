package com.kh.bookfinder.book_trade.api;


import static com.kh.bookfinder.global.constants.HttpErrorMessage.BAD_REQUEST;
import static com.kh.bookfinder.global.constants.HttpErrorMessage.FORBIDDEN;
import static com.kh.bookfinder.global.constants.HttpErrorMessage.UNAUTHORIZED;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kh.bookfinder.auth.jwt.service.JwtService;
import com.kh.bookfinder.book_trade.entity.BookTrade;
import com.kh.bookfinder.book_trade.entity.Status;
import com.kh.bookfinder.book_trade.helper.MockBookTrade;
import com.kh.bookfinder.book_trade.repository.BookTradeRepository;
import com.kh.bookfinder.borough.entity.Borough;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.user.entity.User;
import com.kh.bookfinder.user.entity.UserRole;
import com.kh.bookfinder.user.helper.MockUser;
import com.kh.bookfinder.user.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class ListBookTradeApiTest {

  // 유저 간 책 대출 API, List
  private static final String VALID_ACCESS_TOKEN = "validAccessToken";
  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private BookTradeRepository bookTradeRepository;
  @MockBean
  private UserRepository userRepository;
  @MockBean
  private JwtService jwtService;

  private void mockJwtAuthenticationFilter(User mockUser) {
    // JwtAuthenticationFilter Mocking
    // extractAccessToken() -> "validAccessToken"
    // validateToken() -> true
    // extractEmail() -> mockUser's email;
    when(jwtService.extractAccessToken(any())).thenReturn(VALID_ACCESS_TOKEN);
    when(jwtService.validateToken(VALID_ACCESS_TOKEN)).thenReturn(true);
    when(jwtService.extractEmail(VALID_ACCESS_TOKEN)).thenReturn(mockUser.getEmail());
    // And: UserRepository가 mockUser를 반환
    when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));
  }

  private ResultActions callApiWithAuthorization(Long id) throws Exception {
    return mockMvc
        .perform(
            get("/api/v1/trades/list/{boroughId}", id)
                .header("Authorization", "Bearer " + VALID_ACCESS_TOKEN)
        );
  }

  @Test
  @DisplayName("권한이 있고 DB에 저장된 BookTrade 튜플이 없는 경우")
  public void success_OnAuthorization_WithNoBookTradeData_InDB() throws Exception {
    // Given: 권한이 "ROLE_USER"인 User가 주어진다.
    User mockUser = MockUser.getMockUser();
    mockUser.setRole(UserRole.ROLE_USER);
    Borough mockUserBorough = mockUser.getBorough();

    // Mocking: JwtAuthenticationFilter Mocking
    mockJwtAuthenticationFilter(mockUser);

    // When: List BookTrade API를 호출한다.
    ResultActions resultActions = callApiWithAuthorization(mockUserBorough.getId());

    // Then: Status는 200이다.
    resultActions.andExpect(status().isOk());
    // And: Response Body로 BookTrade List를 반환한다. (Empty)
    resultActions.andExpect(jsonPath("$").isEmpty());
  }

  @Test
  @DisplayName("권한이 있고 DB에 BookTrade 튜플이 있는 경우")
  public void success_OnAuthorization_WithBookTrades_InDB() throws Exception {
    // Given: 권한이 "ROLE_USER"인 User가 주어진다.
    User mockUser = MockUser.getMockUser();
    mockUser.setRole(UserRole.ROLE_USER);
    Borough mockUserBorough = mockUser.getBorough();
    // And: mockBookTrade List가 주어진다.
    List<BookTrade> mockBookTrades = MockBookTrade.getMockBookTradeListOnUser(mockUser, 10);

    // Mocking: JwtAuthenticationFilter Mocking
    mockJwtAuthenticationFilter(mockUser);
    // And: BookTradeRepository가 mockBookTrades를 반환
    when(bookTradeRepository.findByBoroughIdAndDeleteYn(mockUserBorough.getId(), Status.N))
        .thenReturn(mockBookTrades);

    // When: List BookTrade API를 호출한다.
    ResultActions resultActions = callApiWithAuthorization(mockUserBorough.getId());

    // Then: Status는 200 Ok 이다.
    resultActions.andExpect(status().isOk());
    // And: Response Body로 10개의 BookTrade List를 반환한다.
    resultActions.andExpect(jsonPath("$", hasSize(mockBookTrades.size())));
    assertBookTrades(resultActions, mockBookTrades);
  }

  @Test
  @DisplayName("관리자 권한이고 DB에 Delete가 Y인 BookTrade 튜플이 있는 경우")
  public void success_OnAuthorization_WithRoleAdmin_WithBookTrades_DeleteY_InDB() throws Exception {
    // Given: 권한이 "ROLE_ADMIN"인 User가 주어진다.
    User mockUser = MockUser.getMockUser();
    mockUser.setRole(UserRole.ROLE_ADMIN);
    Borough mockUserBorough = mockUser.getBorough();
    // And: deleteYn이 Y인 BookTrade를 5개 포함한 mockBookTrade List가 주어진다.
    List<BookTrade> mockBookTrades = MockBookTrade.getMockBookTradeListOnUserWithDeleted(mockUser, 10, 5);

    // Mocking: JwtAuthenticationFilter Mocking
    mockJwtAuthenticationFilter(mockUser);
    // And: BookTradeRepository가 mockBookTrades를 반환
    when(bookTradeRepository.findByBoroughId(mockUserBorough.getId())).thenReturn(mockBookTrades);

    // When: List BookTrade API를 호출한다.
    ResultActions resultActions = callApiWithAuthorization(mockUserBorough.getId());

    // Then: Status는 200 Ok 이다.
    resultActions.andExpect(status().isOk());
    // And: Response Body로 10개의 BookTrade List를 반환한다.
    resultActions.andExpect(jsonPath("$", hasSize(mockBookTrades.size())));
    assertBookTrades(resultActions, mockBookTrades);
  }

  @Test
  @DisplayName("일반 사용자 권한이고 DB에 Delete가 Y인 BookTrade 튜플이 있는 경우")
  public void success_OnAuthorization_WithRoleUser_WithBookTrades_DeleteY_InDB() throws Exception {
    // Given: 권한이 "ROLE_USER"인 User가 주어진다.
    User mockUser = MockUser.getMockUser();
    mockUser.setRole(UserRole.ROLE_USER);
    Borough mockUserBorough = mockUser.getBorough();
    // And: deleteYn이 Y인 BookTrade를 5개 포함한 mockBookTrade List가 주어진다.
    List<BookTrade> mockBookTrades = MockBookTrade.getMockBookTradeListOnUserWithDeleted(mockUser, 10, 5);

    // Mocking: JwtAuthenticationFilter Mocking
    mockJwtAuthenticationFilter(mockUser);
    // And: BookTradeRepository가 activeMockBookTrades를 반환
    List<BookTrade> activeMockBookTrades = mockBookTrades.stream()
        .filter(x -> x.getDeleteYn() == Status.N)
        .collect(Collectors.toList());
    when(bookTradeRepository.findByBoroughIdAndDeleteYn(mockUserBorough.getId(), Status.N))
        .thenReturn(activeMockBookTrades);

    // When: List BookTrade API를 호출한다.
    ResultActions resultActions = callApiWithAuthorization(mockUserBorough.getId());

    // Then: Status는 200 Ok 이다.
    resultActions.andExpect(status().isOk());
    // And: Response Body로 5개의 BookTrade List를 반환한다.
    resultActions.andExpect(jsonPath("$", hasSize(activeMockBookTrades.size())));
    assertBookTrades(resultActions, activeMockBookTrades);
  }

  @Test
  @DisplayName("관리자 권한이고 사용자의 boroughId와 요청 boroughId가 다른 경우")
  public void fail_OnAuthorization_WithRoleAdmin_WithDifferentBoroughId_OnUserAndRequest() throws Exception {
    // Given: 권한이 "ROLE_ADMIN"인 User가 주어진다.
    User mockUser = MockUser.getMockUser();
    mockUser.setRole(UserRole.ROLE_ADMIN);
    Borough mockUserBorough = mockUser.getBorough();
    // And: mockUser의 boroughId와 다른 boroughId가 주어진다.
    Long boroughId = 1L;
    assert !boroughId.equals(mockUserBorough.getId());
    // And: deleteYn이 Y인 BookTrade를 5개 포함한 mockBookTrade List가 주어진다.
    List<BookTrade> mockBookTrades = MockBookTrade.getMockBookTradeListOnUserWithDeleted(mockUser, 10, 5);

    // Mocking: JwtAuthenticationFilter Mocking
    mockJwtAuthenticationFilter(mockUser);
    // And: BookTradeRepository가 mockBookTrades를 반환
    when(bookTradeRepository.findByBoroughId(boroughId)).thenReturn(mockBookTrades);

    // When: List BookTrade API를 호출한다.
    ResultActions resultActions = callApiWithAuthorization(boroughId);

    // Then: Status는 200 Ok이다.
    resultActions.andExpect(status().isOk());
    // And: Response Body로 10개의 BookTrade List를 반환한다.
    resultActions.andExpect(jsonPath("$", hasSize(mockBookTrades.size())));
    assertBookTrades(resultActions, mockBookTrades);
  }

  @Test
  @DisplayName("일반 사용자 권한이고 사용자의 boroughId와 요청 boroughId가 다른 경우")
  public void fail_OnAuthorization_WithRoleUser_WithDifferentBoroughId_OnUserAndRequest() throws Exception {
    // Given: 권한이 "ROLE_USER"인 User가 주어진다.
    User mockUser = MockUser.getMockUser();
    mockUser.setRole(UserRole.ROLE_USER);
    Borough mockUserBorough = mockUser.getBorough();
    // And: mockUser의 boroughId와 다른 boroughId가 주어진다.
    Long boroughId = 1L;
    assert !boroughId.equals(mockUserBorough.getId());

    // Mocking: JwtAuthenticationFilter Mocking
    mockJwtAuthenticationFilter(mockUser);

    // When: List BookTrade API를 호출한다.
    ResultActions resultActions = callApiWithAuthorization(boroughId);

    // Then: Status는 403 Forbidden이다.
    resultActions.andExpect(status().isForbidden());
    // And: Response Body로 message와 detail을 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(FORBIDDEN.getMessage())));
    resultActions.andExpect(jsonPath("$.detail", is(Message.FORBIDDEN_BOOK_TRADES)));
  }

  @Test
  @DisplayName("권한이 있고 유효하지 않은 boroughId가 주어진 경우")
  public void fail_onInvalidBoroughId() throws Exception {
    // Given: 권한이 "ROLE_USER"인 User가 주어진다.
    User mockUser = MockUser.getMockUser();
    mockUser.setRole(UserRole.ROLE_ADMIN);
    // Given: 유효하지 않은 Borough ID가 주어진다
    Long invalidBoroughId = 3812L;

    // Mocking: JwtAuthenticationFilter Mocking
    mockJwtAuthenticationFilter(mockUser);

    // When: List BookTrade API를 호출한다.
    ResultActions resultActions = callApiWithAuthorization(invalidBoroughId);

    // Then: Status는 400 Bad Request 이다.
    resultActions.andExpect(status().isBadRequest());
    // And: Response Body로 message와 details를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(BAD_REQUEST.getMessage())));
    resultActions.andExpect(jsonPath("$.details.boroughId", is(Message.INVALID_BOROUGH)));
  }

  @Test
  @DisplayName("권한 없이 요청하는 경우")
  public void fail_OnNoAuthorization() throws Exception {
    // Given: 유효한 Borough ID가 주어진다
    Long boroughId = 1L;

    // Mocking: JwtService가 JwtException을 발생
    when(jwtService.validateToken(any())).thenThrow(new JwtException(Message.NOT_LOGIN));

    // When: Header에 Authorization 없이 List BookTrade API를 호출한다.
    ResultActions resultActions = mockMvc
        .perform(get("/api/v1/trades/list/{boroughId}", boroughId));

    // Then: Status는 401 Unauthorized 이다.
    resultActions.andExpect(status().isUnauthorized());
    // And: Response Body로 message와 detail를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(UNAUTHORIZED.getMessage())));
    resultActions.andExpect(jsonPath("$.detail", is(Message.NOT_LOGIN)));
  }

  private void assertBookTrades(ResultActions resultActions, List<BookTrade> mockBookTrades) throws Exception {
    for (int i = 0; i < mockBookTrades.size(); i++) {
      String target = String.format("$.[%d]", i);
      BookTrade bookTrade = mockBookTrades.get(i);
      resultActions.andExpect(jsonPath(target + ".id", is(bookTrade.getId().intValue())));
      resultActions.andExpect(jsonPath(target + ".tradeType", is(bookTrade.getTradeType().name())));
      resultActions.andExpect(jsonPath(target + ".tradeYn", is(bookTrade.getTradeYn().name())));
      resultActions.andExpect(jsonPath(target + ".rentalCost", is(bookTrade.getRentalCost())));
      resultActions.andExpect(jsonPath(target + ".createDate", is(bookTrade.getCreateDate().toString())));

      resultActions.andExpect(jsonPath(target + ".user.nickname", is(bookTrade.getUser().getNickname())));
      resultActions.andExpect(jsonPath(target + ".borough.name", is(bookTrade.getBorough().getName())));

      resultActions.andExpect(jsonPath(target + ".book.name", is(bookTrade.getBook().getName())));
      resultActions.andExpect(jsonPath(target + ".book.authors", is(bookTrade.getBook().getAuthors())));
      resultActions.andExpect(jsonPath(target + ".book.publicationYear", is(bookTrade.getBook().getPublicationYear())));
      resultActions.andExpect(jsonPath(target + ".book.imageUrl", is(bookTrade.getBook().getImageUrl())));
    }
  }
}
