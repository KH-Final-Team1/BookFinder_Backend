package com.kh.bookfinder.book_trade.api;

import static com.kh.bookfinder.global.constants.HttpErrorMessage.FORBIDDEN;
import static com.kh.bookfinder.global.constants.HttpErrorMessage.NOT_FOUND;
import static com.kh.bookfinder.global.constants.HttpErrorMessage.UNAUTHORIZED;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kh.bookfinder.auth.jwt.service.JwtService;
import com.kh.bookfinder.book.entity.Book;
import com.kh.bookfinder.book_trade.entity.BookTrade;
import com.kh.bookfinder.book_trade.entity.Status;
import com.kh.bookfinder.book_trade.repository.BookTradeRepository;
import com.kh.bookfinder.borough.entity.Borough;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.helper.MockBookTrade;
import com.kh.bookfinder.helper.MockUser;
import com.kh.bookfinder.user.entity.User;
import com.kh.bookfinder.user.entity.UserRole;
import com.kh.bookfinder.user.repository.UserRepository;
import io.jsonwebtoken.JwtException;
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
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class GetBookTradeOneApiTest {

  // 유저 간 책 대출 API, Detail
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

  private ResultActions callApiWithAuthorization(Long tradeId) throws Exception {
    return mockMvc
        .perform(
            get("/api/v1/trades/{tradeId}", tradeId)
                .header("Authorization", "Bearer " + VALID_ACCESS_TOKEN)
        );
  }

  @Test
  @DisplayName("권한이 있고 유효한 BookTrade Id가 주어졌을 때")
  public void success_OnValidId_WithAuthorization() throws Exception {
    // Given: 권한이 "ROLE_USER"인 User가 주어진다.
    User mockUser = MockUser.getMockUser();
    mockUser.setRole(UserRole.ROLE_USER);
    // And: mockBookTrade가 주어진다.
    BookTrade mockBookTrade = MockBookTrade.getMockBookTrade();
    // And mockBookTrade와 mockUser의 boroughId가 같다.
    assert mockBookTrade.getBorough().equals(mockUser.getBorough());
    // And: 유효한 BookTrade Id가 주어진다.
    Long validTradeId = mockBookTrade.getId();

    // Mocking: JwtAuthenticationFilter Mocking
    mockJwtAuthenticationFilter(mockUser);
    // And: BookTradeRepository가 mockBookTrade를 반환
    when(bookTradeRepository.findById(validTradeId)).thenReturn(Optional.of(mockBookTrade));

    // When: Get BookTrade API를 호출한다.
    ResultActions resultActions = callApiWithAuthorization(validTradeId);

    // Then: Status는 200 Ok이다.
    resultActions.andExpect(status().isOk());
    // And: Response Body로 BookTrade 1개를 반환한다.
    assertBookTrades(resultActions, mockBookTrade);
  }

  @Test
  @DisplayName("관리자 권한이고 User와 BookTrade의 Borough가 다르게 주어졌을 때")
  public void success_OnDifferentBorough_UserAndBookTrade_WithRoleAdmin() throws Exception {
    // Given: 권한이 "ROLE_ADMIN"인 User가 주어진다.
    User mockUser = MockUser.getMockUser();
    mockUser.setRole(UserRole.ROLE_ADMIN);
    // And: mockBookTrade가 주어진다.
    BookTrade mockBookTrade = MockBookTrade.getMockBookTrade();
    mockBookTrade.setBorough(Borough.builder().id(1L).name("강남구").build());
    // And mockBookTrade와 mockUser의 borough가 다르다.
    assert !mockBookTrade.getBorough().equals(mockUser.getBorough());
    // And: 유효한 BookTrade Id가 주어진다.
    Long validTradeId = mockBookTrade.getId();

    // Mocking: JwtAuthenticationFilter Mocking
    mockJwtAuthenticationFilter(mockUser);
    // And: BookTradeRepository가 mockBookTrade를 반환
    when(bookTradeRepository.findById(validTradeId)).thenReturn(Optional.of(mockBookTrade));

    // When: Get BookTrade API를 호출한다.
    ResultActions resultActions = callApiWithAuthorization(validTradeId);

    // Then: Status는 200 Ok이다.
    resultActions.andExpect(status().isOk());
    // And: Response Body로 BookTrade 1개를 반환한다.
    assertBookTrades(resultActions, mockBookTrade);
  }

  @Test
  @DisplayName("일반 사용자 권한이고 User와 BookTrade의 Borough가 다르게 주어졌을 때")
  public void fail_OnDifferentBorough_UserAndBookTrade_WithRoleUser() throws Exception {
    // Given: 권한이 "ROLE_USER"인 User가 주어진다.
    User mockUser = MockUser.getMockUser();
    mockUser.setRole(UserRole.ROLE_USER);
    // And: mockBookTrade가 주어진다.
    BookTrade mockBookTrade = MockBookTrade.getMockBookTrade();
    mockBookTrade.setBorough(Borough.builder().id(1L).name("강남구").build());
    // And mockBookTrade와 mockUser의 borough가 다르다.
    assert !mockBookTrade.getBorough().equals(mockUser.getBorough());
    // And: 유효한 BookTrade Id가 주어진다.
    Long validTradeId = mockBookTrade.getId();

    // Mocking: JwtAuthenticationFilter Mocking
    mockJwtAuthenticationFilter(mockUser);
    // And: BookTradeRepository가 mockBookTrade를 반환
    when(bookTradeRepository.findById(validTradeId)).thenReturn(Optional.of(mockBookTrade));

    // When: Get BookTrade API를 호출한다.
    ResultActions resultActions = callApiWithAuthorization(validTradeId);

    // Then: Status는 403 Forbidden이다.
    resultActions.andExpect(status().isForbidden());
    // And: Response Body로 message와 detail을 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(FORBIDDEN.getMessage())));
    resultActions.andExpect(jsonPath("$.detail", is(Message.FORBIDDEN_BOOK_TRADES)));
  }

  @Test
  @DisplayName("관리자 권한이고 삭제된 BookTrade의 Id가 주어졌을 때")
  public void success_OnDeleteBookTradeId_WithRoleAdmin() throws Exception {
    // Given: 권한이 "ROLE_ADMIN"인 User가 주어진다.
    User mockUser = MockUser.getMockUser();
    mockUser.setRole(UserRole.ROLE_ADMIN);
    // And: deleted mockBookTrade가 주어진다.
    BookTrade mockBookTrade = MockBookTrade.getMockBookTrade();
    mockBookTrade.setDeleteYn(Status.Y);
    // And: 유효한 BookTrade Id가 주어진다.
    Long validTradeId = mockBookTrade.getId();

    // Mocking: JwtAuthenticationFilter Mocking
    mockJwtAuthenticationFilter(mockUser);
    // And: BookTradeRepository가 mockBookTrade를 반환
    when(bookTradeRepository.findById(validTradeId)).thenReturn(Optional.of(mockBookTrade));

    // When: Get BookTrade API를 호출한다.
    ResultActions resultActions = callApiWithAuthorization(validTradeId);

    // Then: Status는 200 Ok이다.
    resultActions.andExpect(status().isOk());
    // And: Response Body로 BookTrade 1개를 반환한다.
    assertBookTrades(resultActions, mockBookTrade);
  }

  @Test
  @DisplayName("일반 사용자 권한이고 삭제된 BookTrade의 Id가 주어졌을 때")
  public void fail_OnDeleteBookTradeId_WithRoleUser() throws Exception {
    // Given: 권한이 "ROLE_USER"인 User가 주어진다.
    User mockUser = MockUser.getMockUser();
    mockUser.setRole(UserRole.ROLE_USER);
    // And: deleted mockBookTrade가 주어진다.
    BookTrade mockBookTrade = MockBookTrade.getMockBookTrade();
    mockBookTrade.setDeleteYn(Status.Y);
    // And: 유효한 BookTrade Id가 주어진다.
    Long validTradeId = mockBookTrade.getId();

    // Mocking: JwtAuthenticationFilter Mocking
    mockJwtAuthenticationFilter(mockUser);
    // And: BookTradeRepository가 mockBookTrade를 반환
    when(bookTradeRepository.findById(validTradeId)).thenReturn(Optional.of(mockBookTrade));

    // When: Get BookTrade API를 호출한다.
    ResultActions resultActions = callApiWithAuthorization(validTradeId);

    // Then: Status는 404 Not Found이다.
    resultActions.andExpect(status().isNotFound());
    // And: Response Body로 message와 detail을 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(NOT_FOUND.getMessage())));
    resultActions.andExpect(jsonPath("$.detail", is(Message.DELETED_TRADE)));
  }

  @Test
  @DisplayName("권한이 있고 유효하지 않은 BookTrade Id가 주어졌을 때")
  public void fail_OnInvalidBookTradeId_WithNotExistInDB_WithAuthorization() throws Exception {
    // Given: 권한이 "ROLE_ADMIN"인 User가 주어진다.
    User mockUser = MockUser.getMockUser();
    mockUser.setRole(UserRole.ROLE_ADMIN);
    // Given: 유효하지 않은 BookTrade Id가 주어진다.
    Long invalidTradeId = 123123L;

    // Mocking: JwtAuthenticationFilter Mocking
    mockJwtAuthenticationFilter(mockUser);

    // When: Get BookTrade API를 호출한다.
    ResultActions resultActions = callApiWithAuthorization(invalidTradeId);

    // Then: Status는 404 Not Found이다.
    resultActions.andExpect(status().isNotFound());
    // And: Response Body로 message와 detail을 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(NOT_FOUND.getMessage())));
    resultActions.andExpect(jsonPath("$.detail", is(Message.NOT_FOUND_TRADE)));
  }

  @Test
  @DisplayName("권한 없이 요청하는 경우")
  public void fail_OnNoAuthorization() throws Exception {
    // Given: mockBookTrade가 주어진다.
    BookTrade mockBookTrade = MockBookTrade.getMockBookTrade();
    // And: 유효한 BookTrade Id가 주어진다.
    Long validTradeId = mockBookTrade.getId();

    // Mocking: BookTradeRepository가 mockBookTrade를 반환
    when(bookTradeRepository.findById(validTradeId)).thenReturn(Optional.of(mockBookTrade));
    // And: JwtService가 JwtException을 발생
    when(jwtService.validateToken(any())).thenThrow(new JwtException(Message.NOT_LOGIN));

    // When: Header에 Authorization 없이 Get BookTrade API를 호출한다.
    ResultActions resultActions = mockMvc
        .perform(get("/api/v1/trades/{tradeId}", validTradeId));

    // Then: Status는 401 Unauthorized이다.
    resultActions.andExpect(status().isUnauthorized());
    // And: Response Body로 message와 detail을 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(UNAUTHORIZED.getMessage())));
    resultActions.andExpect(jsonPath("$.detail", is(Message.NOT_LOGIN)));
  }

  private void assertBookTrades(ResultActions resultActions, BookTrade bookTrade) throws Exception {
    resultActions.andExpect(jsonPath("$.id", is(bookTrade.getId().intValue())));
    resultActions.andExpect(jsonPath("$.tradeType", is(bookTrade.getTradeType().name())));
    resultActions.andExpect(jsonPath("$.tradeYn", is(bookTrade.getTradeYn().name())));
    resultActions.andExpect(jsonPath("$.deleteYn", is(bookTrade.getDeleteYn().name())));
    resultActions.andExpect(jsonPath("$.content", is(bookTrade.getContent())));
    resultActions.andExpect(jsonPath("$.rentalCost", is(bookTrade.getRentalCost())));
    resultActions.andExpect(jsonPath("$.longitude", is(bookTrade.getLongitude().doubleValue())));
    resultActions.andExpect(jsonPath("$.latitude", is(bookTrade.getLatitude().doubleValue())));
    resultActions.andExpect(jsonPath("$.createDate", is(bookTrade.getCreateDate().toString())));

    resultActions.andExpect(jsonPath("$.user.nickname", is(bookTrade.getUser().getNickname())));

    Book book = bookTrade.getBook();
    resultActions.andExpect(jsonPath("$.book.name", is(book.getName())));
    resultActions.andExpect(jsonPath("$.book.authors", is(book.getAuthors())));
    resultActions.andExpect(jsonPath("$.book.publisher", is(book.getPublisher())));
    resultActions.andExpect(jsonPath("$.book.publicationYear", is(book.getPublicationYear())));
    resultActions.andExpect(jsonPath("$.book.imageUrl", is(book.getImageUrl())));
    resultActions.andExpect(jsonPath("$.book.description", is(book.getDescription())));
  }
}
