package com.kh.bookfinder.book_trade.api;


import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.bookfinder.book_trade.dto.BookTradeListResponseDto;
import com.kh.bookfinder.book_trade.entity.BookTrade;
import com.kh.bookfinder.book_trade.helper.MockBookTrade;
import com.kh.bookfinder.book_trade.service.BookTradeService;
import com.kh.bookfinder.global.constants.Message;
import java.util.ArrayList;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
public class ListBookTradeApiTest {

  // 유저 간 책 대출 API, List

  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private BookTradeService bookTradeService;

  @Test
  @DisplayName("DB에 저장된 BookTrade 튜플이 없는 경우")
  public void listBookTradeSuccessOnEmptyBookTradeList() throws Exception {
    // Given: 유효한 Borough ID가 주어진다
    int boroughId = 1;

    // When: List BookTrade API를 호출한다.
    ResultActions resultActions = mockMvc
        .perform(MockMvcRequestBuilders
            .get("/api/v1/trades/list/{boroughId}", boroughId));
    // Then: Status는 200이다.
    resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    // And: Response Body로 BookTrade List를 반환한다. (Empty)
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
  }

  @Test
  @DisplayName("DB에 BookTrade 튜플이 있는 경우")
  public void listBookTradeSuccessOnExistBookTradeList() throws Exception {
    // Given: 유효한 Borough ID가 주어진다
    // And: Mock BookTrade List가 주어진다.
    int boroughId = 1;
    ArrayList<BookTrade> mockBookTrades = MockBookTrade.getMockBookTradeList(10);
    when(this.bookTradeService.getBookTrades(any()))
        .thenReturn(mockBookTrades);

    // When: List BookTrade API를 호출한다.
    ResultActions resultActions = mockMvc
        .perform(MockMvcRequestBuilders
            .get("/api/v1/trades/list/{boroughId}", boroughId));

    // Then: Status는 200 Ok 이다.
    resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    // And: Response Body로 10개의 BookTrade List를 반환한다.
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(10)));
    // And: list는 jsonExpected와 같다.
    String expected = objectMapper.writeValueAsString(
        mockBookTrades
            .stream()
            .map(x -> x.toResponse(BookTradeListResponseDto.class))
            .collect(Collectors.toList())
    );
    resultActions.andExpect(MockMvcResultMatchers.content().json(expected));
  }

  @Test
  @DisplayName("유효하지 않은 boroughId가 주어진 경우")
  public void listBookTradeFailOnInvalidBoroughId() throws Exception {
    // Given: 유효하지 않은 Borough ID가 주어진다
    int invalidBoroughId = 28;

    // When: List BookTrade API를 호출한다.
    ResultActions resultActions = mockMvc
        .perform(MockMvcRequestBuilders
            .get("/api/v1/trades/list/{boroughId}", invalidBoroughId));

    // Then: Status는 400 Bad Request 이다.
    resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest());
    // And: Response Body로 message와 details를 반환한다.
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.message", is(Message.BAD_REQUEST)));
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.details.boroughId", is(Message.INVALID_BOROUGH)));
  }
}
