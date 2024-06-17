package com.kh.bookfinder.book_trade;

import static com.kh.bookfinder.global.constants.HttpErrorMessage.FORBIDDEN;
import static com.kh.bookfinder.global.constants.HttpErrorMessage.NOT_FOUND;
import static com.kh.bookfinder.global.constants.HttpErrorMessage.UNAUTHORIZED;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kh.bookfinder.auth.jwt.service.JwtService;
import com.kh.bookfinder.book.entity.Book;
import com.kh.bookfinder.book.enums.ApprovalStatus;
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
public class DeleteBookTradeApiTest {

  private static final String VALID_ACCESS_TOKEN = "validAccessToken";
  @Autowired
  private MockMvc mockMvc;
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

  private ResultActions callApiWithAuth(Long bookTradeId) throws Exception {
    return mockMvc.perform(
        delete("/api/v1/trades/{tradeId}", bookTradeId)
            .header("Authorization", "Bearer " + VALID_ACCESS_TOKEN)
    );
  }

  @Test
  @DisplayName("Auth의 User와 BookTrade의 User가 같고, 유효한 TradeId가 주어졌을 때")
  public void success_onValidTradeId_andSameUserAuthWithBookTrade() throws Exception {
    // Given: 유효한 mockBookTrade가 주어진다.
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
    ResultActions resultActions = callApiWithAuth(mockBookTrade.getId());

    // Then: Status는 Ok이다.
    resultActions.andExpect(status().isOk());
    // And: Response Body로 message를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(Message.SUCCESS_DELETE)));
  }

  @Test
  @DisplayName("Auth가 관리자 User고, 유효한 TradeId가 주어졌을 때")
  public void success_onValidTradeId_andAuthWithRoleAdmin() throws Exception {
    // Given: 유효한 mockBookTrade가 주어진다.
    BookTrade mockBookTrade = MockBookTrade.getMockBookTrade();

    // BookTrade 연관 관계 매핑
    User mockUser = MockUser.getMockUser(UserRole.ROLE_USER);
    Book mockBook = MockBook.getMockBook(ApprovalStatus.APPROVE);
    mockBookTrade.setUser(mockUser);
    mockBookTrade.setBook(mockBook);

    // Mocking: accessToken의 User 정보는 mockUser가 아닌 Admin User
    User admin = MockUser.getMockUser(UserRole.ROLE_ADMIN);
    assert !admin.equals(mockUser);
    mockJwtAuthenticationFilter(admin);
    // And: BookTradeRepository가 mockBookTrade를 반환
    when(bookTradeRepository.findById(mockBookTrade.getId())).thenReturn(Optional.of(mockBookTrade));

    // When: Update BookTrade API를 호출한다.
    ResultActions resultActions = callApiWithAuth(mockBookTrade.getId());

    // Then: Status는 Ok이다.
    resultActions.andExpect(status().isOk());
    // And: Response Body로 message를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(Message.SUCCESS_DELETE)));
  }

  @Test
  @DisplayName("유효하지 않은 TradeId가 주어졌을 때")
  public void fail_onInvalidTradeId() throws Exception {
    // Given: 유효한 mockBookTrade가 주어진다.
    BookTrade mockBookTrade = MockBookTrade.getMockBookTrade();
    // And: 유효하지 않은 tradeId가 주어진다.
    Long invalidTradeId = 2342645L;
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
    ResultActions resultActions = callApiWithAuth(invalidTradeId);

    // Then: Status는 Not Found이다.
    resultActions.andExpect(status().isNotFound());
    // And: Response Body로 message와 detail을 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(NOT_FOUND.getMessage())));
    resultActions.andExpect(jsonPath("$.detail", is(Message.NOT_FOUND_TRADE)));
  }

  @Test
  @DisplayName("이미 삭제된 BookTrade의 TradeId가 주어졌을 때")
  public void fail_onInvalidTradeId_withAlreadyDeletedBookTrade() throws Exception {
    // Given: 삭제된 mockBookTrade가 주어진다.
    BookTrade mockBookTrade = MockBookTrade.getMockBookTrade();
    mockBookTrade.setDeleteYn(Status.Y);

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
    ResultActions resultActions = callApiWithAuth(mockBookTrade.getId());

    // Then: Status는 Not Found이다.
    resultActions.andExpect(status().isNotFound());
    // And: Response Body로 message와 detail을 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(NOT_FOUND.getMessage())));
    resultActions.andExpect(jsonPath("$.detail", is(Message.DELETED_TRADE)));
  }

  @Test
  @DisplayName("Auth가 관리자 User가 아니고, BookTrade의 User와 다를 때")
  public void fail_onValidTradeId_withDifferentUserAuthWithBookTrade() throws Exception {
    // Given: 유효한 mockBookTrade가 주어진다.
    BookTrade mockBookTrade = MockBookTrade.getMockBookTrade();

    // BookTrade 연관 관계 매핑
    User mockUser = MockUser.getMockUser(UserRole.ROLE_ADMIN);
    Book mockBook = MockBook.getMockBook(ApprovalStatus.APPROVE);
    mockBookTrade.setUser(mockUser);
    mockBookTrade.setBook(mockBook);

    // Mocking: accessToken의 User 정보는 mockUser가 아닌 다른 User
    User otherUser = MockUser.getMockUser(UserRole.ROLE_USER);
    assert !otherUser.equals(mockUser);
    mockJwtAuthenticationFilter(otherUser);
    // And: BookTradeRepository가 mockBookTrade를 반환
    when(bookTradeRepository.findById(mockBookTrade.getId())).thenReturn(Optional.of(mockBookTrade));

    // When: Update BookTrade API를 호출한다.
    ResultActions resultActions = callApiWithAuth(mockBookTrade.getId());

    // Then: Status는 Forbidden이다.
    resultActions.andExpect(status().isForbidden());
    // And: Response Body로 message와 detail을 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(FORBIDDEN.getMessage())));
    resultActions.andExpect(jsonPath("$.detail", is(Message.FORBIDDEN_BOOK_TRADES_UPDATE)));
  }

  @Test
  @DisplayName("권한 없이 요청할 때")
  public void fail_onNoAuth() throws Exception {
    // Given: 유효한 mockBookTrade가 주어진다.
    BookTrade mockBookTrade = MockBookTrade.getMockBookTrade();

    // BookTrade 연관 관계 매핑
    User mockUser = MockUser.getMockUser(UserRole.ROLE_ADMIN);
    Book mockBook = MockBook.getMockBook(ApprovalStatus.APPROVE);
    mockBookTrade.setUser(mockUser);
    mockBookTrade.setBook(mockBook);

    // Mocking: JwtService가 JwtException을 발생
    when(jwtService.validateToken(any())).thenThrow(new JwtException(Message.NOT_LOGIN));

    // And: BookTradeRepository가 mockBookTrade를 반환
    when(bookTradeRepository.findById(mockBookTrade.getId())).thenReturn(Optional.of(mockBookTrade));

    // When: Update BookTrade API를 호출한다.
    ResultActions resultActions = callApiWithAuth(mockBookTrade.getId());

    // Then: Status는 Unauthorized이다.
    resultActions.andExpect(status().isUnauthorized());
    // And: Response Body로 message와 detail을 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(UNAUTHORIZED.getMessage())));
    resultActions.andExpect(jsonPath("$.detail", is(Message.NOT_LOGIN)));
  }
}
