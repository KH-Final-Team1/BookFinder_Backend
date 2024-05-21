package com.kh.bookfinder.book_trade.api;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.bookfinder.auth.helper.MockToken;
import com.kh.bookfinder.book_trade.dto.BookTradeDetailResponseDto;
import com.kh.bookfinder.book_trade.entity.BookTrade;
import com.kh.bookfinder.book_trade.entity.Status;
import com.kh.bookfinder.book_trade.helper.MockBookTrade;
import com.kh.bookfinder.book_trade.repository.BookTradeRepository;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.user.helper.MockUser;
import com.kh.bookfinder.user.repository.UserRepository;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
public class GetBookTradeOneApiTest {

  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private BookTradeRepository bookTradeRepository;
  @MockBean
  private UserRepository userRepository;

  @Test
  @DisplayName("유효한 BookTrade Id가 주어졌을 때")
  public void getBookTradeOneSuccessOnValidId() throws Exception {
    // Given: 유효한 BookTrade Id가 주어진다.
    long tradeId = 1;
    // And: Mock BookTrade가 주어진다.
    BookTrade mockBookTrade = MockBookTrade.getMockBookTrade();
    when(bookTradeRepository.findById(tradeId))
        .thenReturn(Optional.of(mockBookTrade));
    // And: UserRepository를 Mocking한다.
    when(userRepository.findByEmail(any()))
        .thenReturn(Optional.of(MockUser.getMockUser()));

    // When: Get BookTrade API를 호출한다.
    ResultActions resultActions = mockMvc
        .perform(MockMvcRequestBuilders
            .get("/api/v1/trades/{tradeId}", tradeId)
            .header("Authorization", MockToken.mockAccessToken));

    // Then: Status는 200 Ok이다.
    resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    // And: Response Body로 BookTrade 1개를 반환한다.
    String expected = objectMapper.writeValueAsString(mockBookTrade.toResponse(BookTradeDetailResponseDto.class));
    resultActions.andExpect(MockMvcResultMatchers.content().json(expected));
    checkDetailResponseDto(resultActions);
  }

  @Test
  @DisplayName("DB에 존재하지 않은 BookTrade Id가 주어졌을 때")
  public void getBookTradeOneFailOnIdNotExistInDb() throws Exception {
    // Given: 유효하지 않은 BookTrade Id가 주어진다.
    long invalidTradeId = 123;
    // And: UserRepository를 Mocking한다.
    when(userRepository.findByEmail(any()))
        .thenReturn(Optional.of(MockUser.getMockUser()));

    // When: Get BookTrade API를 호출한다.
    ResultActions resultActions = mockMvc
        .perform(MockMvcRequestBuilders
            .get("/api/v1/trades/{tradeId}", invalidTradeId)
            .header("Authorization", MockToken.mockAccessToken));

    // Then: Status는 404 Not Found이다.
    resultActions.andExpect(MockMvcResultMatchers.status().isNotFound());
    // And: Response Body로 message와 detail을 반환한다.
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.message", is(Message.NOT_FOUND)));
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.detail", is(Message.NOT_FOUND_TRADE)));
  }

  @Test
  @DisplayName("상태가 delete인 BookTrade Id가 주어졌을 때")
  public void getBookTradeOneFailOnIdStatusIsDelete() throws Exception {
    // Given: 유효한 BookTrade Id가 주어진다.
    long tradeId = 1;
    // And: Mock BookTrade가 주어진다.
    BookTrade mockBookTrade = MockBookTrade.getMockBookTrade();
    mockBookTrade.setDeleteYn(Status.Y);
    when(bookTradeRepository.findById(tradeId))
        .thenReturn(Optional.of(mockBookTrade));
    // And: UserRepository를 Mocking한다.
    when(userRepository.findByEmail(any()))
        .thenReturn(Optional.of(MockUser.getMockUser()));

    // When: Get BookTrade API를 호출한다.
    ResultActions resultActions = mockMvc
        .perform(MockMvcRequestBuilders
            .get("/api/v1/trades/{tradeId}", tradeId)
            .header("Authorization", MockToken.mockAccessToken));

    // Then: Status는 404 Not Found이다.
    resultActions.andExpect(MockMvcResultMatchers.status().isNotFound());
    // And: Response Body로 message와 detail을 반환한다.
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.message", is(Message.NOT_FOUND)));
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.detail", is(Message.DELETED_TRADE)));
  }

  @Test
  @DisplayName("Header에 Authorization이 없는 경우")
  public void getBookTradeOneFailTestOnNotExistAuthorizationInHeader() throws Exception {
    // Given: 유효한 BookTrade Id가 주어진다.
    long tradeId = 1;
    // And: Mock BookTrade가 주어진다.
    BookTrade mockBookTrade = MockBookTrade.getMockBookTrade();
    when(bookTradeRepository.findById(tradeId))
        .thenReturn(Optional.of(mockBookTrade));

    // When: Header에 Authorization 없이 Get BookTrade API를 호출한다.
    ResultActions resultActions = mockMvc
        .perform(MockMvcRequestBuilders
            .get("/api/v1/trades/{tradeId}", tradeId));

    // Then: Status는 401 Unauthorized이다.
    resultActions.andExpect(MockMvcResultMatchers.status().isUnauthorized());
    // And: Response Body로 message와 detail을 반환한다.
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.message", is(Message.UNAUTHORIZED)));
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.detail", is(Message.NOT_LOGIN)));
  }

  private void checkDetailResponseDto(ResultActions resultActions) throws Exception {
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.tradeType").exists());
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.tradeYn").exists());
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.content").exists());
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.rentalCost").exists());
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.longitude").exists());
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.latitude").exists());
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.createdDate").exists());
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.book.name").exists());
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.book.authors").exists());
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.book.publisher").exists());
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.book.publicationYear").exists());
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.book.imageUrl").exists());
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.book.description").exists());
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.user.nickname").exists());
  }
}
