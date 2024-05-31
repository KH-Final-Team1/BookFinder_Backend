package com.kh.bookfinder.book_trade;

import static com.kh.bookfinder.global.constants.HttpErrorMessage.BAD_REQUEST;
import static com.kh.bookfinder.global.constants.HttpErrorMessage.FORBIDDEN;
import static com.kh.bookfinder.global.constants.HttpErrorMessage.NOT_FOUND;
import static com.kh.bookfinder.global.constants.HttpErrorMessage.UNAUTHORIZED;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.bookfinder.auth.jwt.service.JwtService;
import com.kh.bookfinder.book.entity.ApprovalStatus;
import com.kh.bookfinder.book.entity.Book;
import com.kh.bookfinder.book_trade.dto.BookTradeYnDto;
import com.kh.bookfinder.book_trade.entity.BookTrade;
import com.kh.bookfinder.book_trade.entity.Status;
import com.kh.bookfinder.book_trade.repository.BookTradeRepository;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.helper.MockBook;
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
public class ChangeBookTradeStatusApiTest {

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

  private ResultActions callApiWithAuth(BookTradeYnDto validRequestDto, Long bookTradeId) throws Exception {
    String requestBody = objectMapper.writeValueAsString(validRequestDto);
    return mockMvc.perform(
        patch("/api/v1/trades/{tradeId}", bookTradeId)
            .header("Authorization", "Bearer " + VALID_ACCESS_TOKEN)
            .contentType("application/json")
            .content(requestBody)
    );
  }

  @Test
  @DisplayName("Auth의 User와 BookTrade의 User가 같고, 유효한 TradeYnDto와 유효한 TradeId가 주어졌을 때")
  public void success_onValidTradeYnDto_andValidTradeId_andSameUserAuthWithBookTrade() throws Exception {
    // Given: 유효한 BookTradeYnDto가 주어진다.
    BookTradeYnDto validRequestDto = new BookTradeYnDto(Status.Y);
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
    ResultActions resultActions = callApiWithAuth(validRequestDto, mockBookTrade.getId());

    // Then: Status는 Ok이다.
    resultActions.andExpect(status().isOk());
    // And: Response Body로 message를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(Message.SUCCESS_CHANGE)));
  }

  @Test
  @DisplayName("유효하지 않은 TradeId가 주어졌을 때")
  public void fail_onInvalidTradeId() throws Exception {
    // Given: 유효한 BookTradeYnDto가 주어진다.
    BookTradeYnDto validRequestDto = new BookTradeYnDto(Status.Y);
    // And: 유효하지 않은 tradeId가 주어진다.
    Long invalidTradeId = 123472L;
    // And: 유효한 mockBookTrade가 주어진다.
    BookTrade mockBookTrade = MockBookTrade.getMockBookTrade();
    assert !invalidTradeId.equals(mockBookTrade.getId());

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
    ResultActions resultActions = callApiWithAuth(validRequestDto, invalidTradeId);

    // Then: Status는 Not Found이다.
    resultActions.andExpect(status().isNotFound());
    // And: Response Body로 message와 detail를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(NOT_FOUND.getMessage())));
    resultActions.andExpect(jsonPath("$.detail", is(Message.NOT_FOUND_TRADE)));
  }

  @Test
  @DisplayName("유효하지 않은 TradeYnDto가 주어졌을 때")
  public void fail_onInvalidTradeYnDto() throws Exception {
    // Given: 유효하지 않은 BookTradeYnDto가 주어진다.
    BookTradeYnDto invalidRequestDto = new BookTradeYnDto();
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
    // And: Response Body로 message와 details를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(BAD_REQUEST.getMessage())));
    resultActions.andExpect(jsonPath("$.details.tradeYn", containsString("null")));
  }

  @Test
  @DisplayName("Auth와 BookTrade의 User가 다를 때")
  public void fail_onDifferentUserAuthWithBookTrade() throws Exception {
    // Given: 유효한 않은 BookTradeYnDto가 주어진다.
    BookTradeYnDto validRequestDto = new BookTradeYnDto(Status.Y);
    // And: 유효한 mockBookTrade가 주어진다.
    BookTrade mockBookTrade = MockBookTrade.getMockBookTrade();

    // BookTrade 연관 관계 매핑
    User mockUser = MockUser.getMockUser(UserRole.ROLE_USER);
    Book mockBook = MockBook.getMockBook(ApprovalStatus.APPROVE);
    mockBookTrade.setUser(mockUser);
    mockBookTrade.setBook(mockBook);

    // Mocking: accessToken의 User 정보는 mockUser가 아닌 다른 User
    User otherUser = MockUser.getMockUser(UserRole.ROLE_ADMIN);
    assert !mockUser.equals(otherUser);
    mockJwtAuthenticationFilter(otherUser);
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
  public void fail_onNoAuth() throws Exception {
    // Given: 유효한 않은 BookTradeYnDto가 주어진다.
    BookTradeYnDto validRequestDto = new BookTradeYnDto(Status.Y);
    // And: 유효한 mockBookTrade가 주어진다.
    BookTrade mockBookTrade = MockBookTrade.getMockBookTrade();

    // BookTrade 연관 관계 매핑
    User mockUser = MockUser.getMockUser(UserRole.ROLE_USER);
    Book mockBook = MockBook.getMockBook(ApprovalStatus.APPROVE);
    mockBookTrade.setUser(mockUser);
    mockBookTrade.setBook(mockBook);

    // Mocking: JwtService가 JwtException을 발생
    when(jwtService.validateToken(any())).thenThrow(new JwtException(Message.NOT_LOGIN));
    // And: BookTradeRepository가 mockBookTrade를 반환
    when(bookTradeRepository.findById(mockBookTrade.getId())).thenReturn(Optional.of(mockBookTrade));

    // When: Update BookTrade API를 호출한다.
    ResultActions resultActions = callApiWithAuth(validRequestDto, mockBookTrade.getId());

    // Then: Status는 Unauthorized이다.
    resultActions.andExpect(status().isUnauthorized());
    // And: Response Body로 message와 detail을 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(UNAUTHORIZED.getMessage())));
    resultActions.andExpect(jsonPath("$.detail", is(Message.NOT_LOGIN)));
  }
}
