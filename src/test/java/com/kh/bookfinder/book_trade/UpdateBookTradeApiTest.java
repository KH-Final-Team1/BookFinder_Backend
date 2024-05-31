package com.kh.bookfinder.book_trade;

import static com.kh.bookfinder.global.constants.HttpErrorMessage.BAD_REQUEST;
import static com.kh.bookfinder.global.constants.HttpErrorMessage.FORBIDDEN;
import static com.kh.bookfinder.global.constants.HttpErrorMessage.NOT_FOUND;
import static com.kh.bookfinder.global.constants.HttpErrorMessage.UNAUTHORIZED;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.bookfinder.auth.jwt.service.JwtService;
import com.kh.bookfinder.book.entity.ApprovalStatus;
import com.kh.bookfinder.book.entity.Book;
import com.kh.bookfinder.book_trade.dto.BookTradeRequestDto;
import com.kh.bookfinder.book_trade.entity.BookTrade;
import com.kh.bookfinder.book_trade.repository.BookTradeRepository;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.helper.MockBook;
import com.kh.bookfinder.helper.MockBookTrade;
import com.kh.bookfinder.helper.MockUser;
import com.kh.bookfinder.helper.RequestDto;
import com.kh.bookfinder.user.entity.User;
import com.kh.bookfinder.user.entity.UserRole;
import com.kh.bookfinder.user.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import java.sql.Date;
import java.time.LocalDate;
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
public class UpdateBookTradeApiTest {

  private static final String VALID_ACCESS_TOKEN = "validAccessToken";
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @MockBean
  private JwtService jwtService;
  @MockBean
  private UserRepository userRepository;
  @MockBean
  private BookTradeRepository bookTradeRepository;

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

  private ResultActions callApiWithAuth(BookTradeRequestDto validRequestDto, Long bookTradeId) throws Exception {
    String requestBody = objectMapper.writeValueAsString(validRequestDto);
    return mockMvc.perform(
        put("/api/v1/trades/{tradeId}", bookTradeId)
            .header("Authorization", "Bearer " + VALID_ACCESS_TOKEN)
            .contentType("application/json")
            .content(requestBody)
    );
  }

  @Test
  @DisplayName("Auth의 User와 BookTrade의 User가 같고, 유효한 BookTradeRequestDto와 유효한 TradeId가 주어졌을 때")
  public void success_onValidBookTradeRequestDto_andValidTradeId_andSameUserAuthWithBookTrade() throws Exception {
    // Given: 유효한 BookTradeRequestDto가 주어진다.
    BookTradeRequestDto validRequestDto = RequestDto.updateBookTradeRequestDto();
    // And: 유효한 mockBookTrade가 주어진다.
    BookTrade mockBookTrade = MockBookTrade.getMockBookTrade();

    // BookTrade 연관 관계 매핑
    User mockUser = MockUser.getMockUser(UserRole.ROLE_USER);
    Book mockBook = MockBook.getMockBook(ApprovalStatus.APPROVE);
    mockBookTrade.setUser(mockUser);
    mockBookTrade.setBook(mockBook);

    // Mocking: accessToken의 User User 정보는 mockUser
    mockJwtAuthenticationFilter(mockUser);
    // And: BookTradeRepository가 mockBookTrade를 반환
    when(bookTradeRepository.findById(mockBookTrade.getId())).thenReturn(Optional.of(mockBookTrade));

    // When: Update BookTrade API를 호출한다.
    ResultActions resultActions = callApiWithAuth(validRequestDto, mockBookTrade.getId());

    // Then: Status는 Ok이다.
    resultActions.andExpect(status().isOk());
    // And: Response Body로 message를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(Message.SUCCESS_UPDATE)));
  }

  @Test
  @DisplayName("유효하지 않은 rentalCost가 주어졌을 때")
  public void fail_onInvalidBookTradeRequestDto_withRentalCost() throws Exception {
    // Given: 유효하지 않은 BookTradeRequestDto가 주어진다. (rentalCost)
    BookTradeRequestDto invalidRequestDto = RequestDto.updateBookTradeRequestDto();
    invalidRequestDto.setRentalCost(-123);
    // And: 유효한 mockBookTrade가 주어진다.
    BookTrade mockBookTrade = MockBookTrade.getMockBookTrade();

    // BookTrade 연관 관계 매핑
    User mockUser = MockUser.getMockUser(UserRole.ROLE_USER);
    Book mockBook = MockBook.getMockBook(ApprovalStatus.APPROVE);
    mockBookTrade.setUser(mockUser);
    mockBookTrade.setBook(mockBook);

    // Mocking: accessToken의 User 정보는 mockUser
    mockJwtAuthenticationFilter(mockUser);
    // And: BookTradeRepository가 mockBookTrade를 반환
    when(bookTradeRepository.findById(mockBookTrade.getId())).thenReturn(Optional.of(mockBookTrade));

    // When: Update BookTrade API를 호출한다.
    ResultActions resultActions = callApiWithAuth(invalidRequestDto, mockBookTrade.getId());

    // Then: Status는 Bad Request이다.
    resultActions.andExpect(status().isBadRequest());
    // And: Response Body로 messag와 details를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(BAD_REQUEST.getMessage())));
    resultActions.andExpect(jsonPath("$.details.rentalCost", is(Message.INVALID_COST)));
  }

  @Test
  @DisplayName("유효하지 않은 limitedDate가 주어졌을 때")
  public void fail_onInvalidBookTradeRequestDto_withLimitedDate() throws Exception {
    // Given: 유효하지 않은 BookTradeRequestDto가 주어진다. (limitedDate)
    BookTradeRequestDto invalidRequestDto = RequestDto.updateBookTradeRequestDto();
    invalidRequestDto.setLimitedDate(Date.valueOf(LocalDate.now().minusDays(1)));
    // And: 유효한 mockBookTrade가 주어진다.
    BookTrade mockBookTrade = MockBookTrade.getMockBookTrade();

    // BookTrade 연관 관계 매핑
    User mockUser = MockUser.getMockUser(UserRole.ROLE_USER);
    Book mockBook = MockBook.getMockBook(ApprovalStatus.APPROVE);
    mockBookTrade.setUser(mockUser);
    mockBookTrade.setBook(mockBook);

    // Mocking: accessToken의 User 정보는 mockUser
    mockJwtAuthenticationFilter(mockUser);
    // And: BookTradeRepository가 mockBookTrade를 반환
    when(bookTradeRepository.findById(mockBookTrade.getId())).thenReturn(Optional.of(mockBookTrade));

    // When: Update BookTrade API를 호출한다.
    ResultActions resultActions = callApiWithAuth(invalidRequestDto, mockBookTrade.getId());

    // Then: Status는 Bad Request이다.
    resultActions.andExpect(status().isBadRequest());
    // And: Response Body로 messag와 details를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(BAD_REQUEST.getMessage())));
    resultActions.andExpect(jsonPath("$.details.limitedDate", is(Message.INVALID_LIMITED_DATE)));
  }

  @Test
  @DisplayName("유효하지 않은 TradeId가 주어졌을 때")
  public void fail_onInvalidTradeId() throws Exception {
    // Given: 유효한 BookTradeRequestDto가 주어진다.
    BookTradeRequestDto validRequestDto = RequestDto.updateBookTradeRequestDto();
    // And: 유효하지 않은 tradeId가 주어진다.
    Long invalidTradeId = 23424L;

    // Mocking: JwtAuthenticationFilter User 정보는 mockUser
    mockJwtAuthenticationFilter(MockUser.getMockUser(UserRole.ROLE_ADMIN));

    // When: Update BookTrade API를 호출한다.
    ResultActions resultActions = callApiWithAuth(validRequestDto, invalidTradeId);

    // Then: Status는 Not Found이다.
    resultActions.andExpect(status().isNotFound());
    // And: Response Body로 messag와 details를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(NOT_FOUND.getMessage())));
    resultActions.andExpect(jsonPath("$.detail", is(Message.NOT_FOUND_TRADE)));
  }

  @Test
  @DisplayName("Auth의 User와 BookTrade의 User가 다를 때")
  public void fail_onValidBookTradeRequestDto_andDifferentUserAuthWithBookTrade() throws Exception {
    // Given: 유효한 BookTradeRequestDto가 주어진다.
    BookTradeRequestDto validRequestDto = RequestDto.updateBookTradeRequestDto();
    // And: 유효한 mockBookTrade가 주어진다.
    BookTrade mockBookTrade = MockBookTrade.getMockBookTrade();

    // BookTrade 연관 관계 매핑
    User mockUser = MockUser.getMockUser(UserRole.ROLE_USER);
    Book mockBook = MockBook.getMockBook(ApprovalStatus.APPROVE);
    mockBookTrade.setUser(mockUser);
    mockBookTrade.setBook(mockBook);

    // Mocking: accessToken의 User 정보는 mockUser가 아닌 다른 User
    User otherMockUser = MockUser.getMockUser(UserRole.ROLE_ADMIN);
    assert !otherMockUser.equals(mockUser);
    mockJwtAuthenticationFilter(otherMockUser);
    // And: BookTradeRepository가 mockBookTrade를 반환
    when(bookTradeRepository.findById(mockBookTrade.getId())).thenReturn(Optional.of(mockBookTrade));

    // When: Update BookTrade API를 호출한다.
    ResultActions resultActions = callApiWithAuth(validRequestDto, mockBookTrade.getId());

    // Then: Status는 Forbidden이다.
    resultActions.andExpect(status().isForbidden());
    // And: Response Body로 message와 detail을 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(FORBIDDEN.getMessage())));
    resultActions.andExpect(jsonPath("$.detail", is(Message.FORBIDDEN_BOOK_TRADES_UPDATE)));
  }

  @Test
  @DisplayName("권한 없이 요청할 때")
  public void fail_onNoAuthorization() throws Exception {
    // Given: 유효한 BookTradeRequestDto가 주어진다.
    BookTradeRequestDto validRequestDto = RequestDto.updateBookTradeRequestDto();
    // And: 유효한 tradeId가 주어진다.
    Long validTradeId = 1L;

    // Mocking: JwtService가 JwtException을 발생
    when(jwtService.validateToken(any())).thenThrow(new JwtException(Message.NOT_LOGIN));

    // When: Update BookTrade API를 호출한다.
    ResultActions resultActions = callApiWithAuth(validRequestDto, validTradeId);

    // Then: Status는 Unauthorized이다.
    resultActions.andExpect(status().isUnauthorized());
    // And: Response Body로 message와 detail을 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(UNAUTHORIZED.getMessage())));
    resultActions.andExpect(jsonPath("$.detail", is(Message.NOT_LOGIN)));
  }
}
