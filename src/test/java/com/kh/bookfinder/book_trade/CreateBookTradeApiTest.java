package com.kh.bookfinder.book_trade;

import static com.kh.bookfinder.global.constants.HttpErrorMessage.BAD_REQUEST;
import static com.kh.bookfinder.global.constants.HttpErrorMessage.NOT_FOUND;
import static com.kh.bookfinder.global.constants.HttpErrorMessage.UNAUTHORIZED;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.bookfinder.auth.jwt.service.JwtService;
import com.kh.bookfinder.book.entity.ApprovalStatus;
import com.kh.bookfinder.book.repository.BookRepository;
import com.kh.bookfinder.book_trade.dto.BookTradeRequestDto;
import com.kh.bookfinder.book_trade.repository.BookTradeRepository;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.helper.MockBook;
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
public class CreateBookTradeApiTest {

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
  @MockBean
  private BookRepository bookRepository;

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

  private ResultActions callApiWithAuthorization(BookTradeRequestDto requestDto) throws Exception {
    String requestBody = objectMapper.writeValueAsString(requestDto);

    return mockMvc.perform(
        post("/api/v1/trades")
            .header("Authorization", "Bearer " + VALID_ACCESS_TOKEN)
            .content(requestBody)
            .contentType("application/json")
    );
  }

  @Test
  @DisplayName("권한이 있고 유효한 BookTradeRequestDto가 주어졌을 때")
  public void success_onValidBookTradeRequestDto_withAuthorization() throws Exception {
    // Given: 유효한 BookTradeRequestDto가 주어진다.
    BookTradeRequestDto requestDto = RequestDto.baseBookTradeRequestDto();

    // Mocking: JwtAuthenticationFilter User 정보는 mockUser
    mockJwtAuthenticationFilter(MockUser.getMockUser(UserRole.ROLE_USER));
    // And: BookRepository가 mockBookTrade의 Book을 반환
    when(bookRepository.findByIsbnAndApprovalStatus(requestDto.getIsbn(), ApprovalStatus.APPROVE))
        .thenReturn(Optional.of(MockBook.getMockBook(ApprovalStatus.APPROVE)));

    // When: Create BookTrade API를 호출한다.
    ResultActions resultActions = callApiWithAuthorization(requestDto);

    // Then: Status는 Created이다.
    resultActions.andExpect(status().isCreated());
  }

  @Test
  @DisplayName("유효하지 않은 Isbn이 주어졌을 때")
  public void fail_onInvalidBookTradeRequestDto_WithIsbn() throws Exception {
    // Given: 유효하지 않은 BookTradeRequestDto가 주어진다. (isbn)
    BookTradeRequestDto invalidRequestDto = RequestDto.baseBookTradeRequestDto();
    invalidRequestDto.setIsbn(17283234792L);

    // Mocking: JwtAuthenticationFilter User 정보는 mockUser
    mockJwtAuthenticationFilter(MockUser.getMockUser(UserRole.ROLE_ADMIN));

    // When: Create BookTrade API를 호출한다.
    ResultActions resultActions = callApiWithAuthorization(invalidRequestDto);

    // Then: Status는 Bad Request이다.
    resultActions.andExpect(status().isBadRequest());
    // And: Response Body로 message와 details를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(BAD_REQUEST.getMessage())));
    resultActions.andExpect(jsonPath("$.details.isbn", is(Message.INVALID_ISBN_DIGITS)));
  }

  @Test
  @DisplayName("유효한 Isbn에 해당하는 Book이 없을 때")
  public void fail_onNotExistBookInDB_WithIsbn() throws Exception {
    // Given: 유효한 BookTradeRequestDto가 주어진다.
    BookTradeRequestDto validRequestDto = RequestDto.baseBookTradeRequestDto();

    // Mocking: JwtAuthenticationFilter User 정보는 mockUser
    mockJwtAuthenticationFilter(MockUser.getMockUser(UserRole.ROLE_ADMIN));

    // When: Create BookTrade API를 호출한다.
    ResultActions resultActions = callApiWithAuthorization(validRequestDto);

    // Then: Status는 Not Found이다.
    resultActions.andExpect(status().isNotFound());
    // And: Response Body로 message와 detail을 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(NOT_FOUND.getMessage())));
    resultActions.andExpect(jsonPath("$.detail", is(Message.NOT_FOUND_BOOK)));
  }

  @Test
  @DisplayName("유효한 Isbn에 해당하는 Book의 상태가 Approve가 아닐 때")
  public void fail_onNotApporvedBook_WithIsbn() throws Exception {
    // Given: 유효한 BookTradeRequestDto가 주어진다.
    BookTradeRequestDto validRequestDto = RequestDto.baseBookTradeRequestDto();

    // Mocking: JwtAuthenticationFilter User 정보는 mockUser
    mockJwtAuthenticationFilter(MockUser.getMockUser(UserRole.ROLE_ADMIN));
    // And: BookRepository가 mockBook을 반환
    when(bookRepository.findByIsbn(validRequestDto.getIsbn()))
        .thenReturn(Optional.of(MockBook.getMockBook(ApprovalStatus.WAIT)));

    // When: Create BookTrade API를 호출한다.
    ResultActions resultActions = callApiWithAuthorization(validRequestDto);

    // Then: Status는 Not Found이다.
    resultActions.andExpect(status().isNotFound());
    // And: Response Body로 message와 detail을 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(NOT_FOUND.getMessage())));
    resultActions.andExpect(jsonPath("$.detail", is(Message.NOT_FOUND_BOOK)));
  }

  @Test
  @DisplayName("유효하지 않은 rentalCost가 주어졌을 때")
  public void fail_onInvalidBookTradeRequestDto_WithRentalCost() throws Exception {
    // Given: 유효하지 않은 BookTradeRequestDto가 주어진다. (rentalCost)
    BookTradeRequestDto invalidRequestDto = RequestDto.baseBookTradeRequestDto();
    invalidRequestDto.setRentalCost(null);

    // Mocking: JwtAuthenticationFilter User 정보는 mockUser
    mockJwtAuthenticationFilter(MockUser.getMockUser(UserRole.ROLE_ADMIN));

    // When: Create BookTrade API를 호출한다.
    ResultActions resultActions = callApiWithAuthorization(invalidRequestDto);

    // Then: Status는 Bad Request이다.
    resultActions.andExpect(status().isBadRequest());
    // And: Response Body로 message와 detail을 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(BAD_REQUEST.getMessage())));
    resultActions.andExpect(jsonPath("$.details.rentalCost", is(Message.INVALID_COST)));
  }

  @Test
  @DisplayName("유효하지 않은 limitedDate가 주어졌을 때")
  public void fail_onInvalidBookTradeRequestDto_WithLimitedDate() throws Exception {
    // Given: 유효하지 않은 BookTradeRequestDto가 주어진다. (limitedDate)
    BookTradeRequestDto invalidRequestDto = RequestDto.baseBookTradeRequestDto();
    invalidRequestDto.setLimitedDate(Date.valueOf(LocalDate.now().minusDays(1)));

    // Mocking: JwtAuthenticationFilter User 정보는 mockUser
    mockJwtAuthenticationFilter(MockUser.getMockUser(UserRole.ROLE_ADMIN));

    // When: Create BookTrade API를 호출한다.
    ResultActions resultActions = callApiWithAuthorization(invalidRequestDto);

    // Then: Status는 Bad Request이다.
    resultActions.andExpect(status().isBadRequest());
    // And: Response Body로 message와 detail을 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(BAD_REQUEST.getMessage())));
    resultActions.andExpect(jsonPath("$.details.limitedDate", is(Message.INVALID_LIMITED_DATE)));
  }

  @Test
  @DisplayName("권한 없이 요청했을 때")
  public void fail_onNoAuthorization() throws Exception {
    // And: 유효한 BookTradeRequestDto가 주어진다.
    BookTradeRequestDto invalidRequestDto = RequestDto.baseBookTradeRequestDto();

    // Mocking: JwtService가 JwtException을 발생
    when(jwtService.validateToken(any())).thenThrow(new JwtException(Message.NOT_LOGIN));

    // When: 권한 없이 Create BookTrade API를 호출한다.
    ResultActions resultActions = mockMvc.perform(post("/api/v1/trades")
        .content(objectMapper.writeValueAsString(invalidRequestDto))
        .contentType("application/json")
    );

    // Then: Status는 Unauthorized이다.
    resultActions.andExpect(status().isUnauthorized());
    // And: Response Body로 message와 detail을 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(UNAUTHORIZED.getMessage())));
    resultActions.andExpect(jsonPath("$.detail", is(Message.NOT_LOGIN)));
  }
}
