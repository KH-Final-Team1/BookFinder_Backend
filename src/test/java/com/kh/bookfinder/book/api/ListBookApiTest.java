package com.kh.bookfinder.book.api;

import static com.kh.bookfinder.global.constants.HttpErrorMessage.BAD_REQUEST;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kh.bookfinder.book.dto.BookListRequestDto;
import com.kh.bookfinder.book.entity.Book;
import com.kh.bookfinder.book.repository.BookRepository;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.helper.MockBook;
import com.kh.bookfinder.helper.RequestDto;
import java.util.List;
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
public class ListBookApiTest {
  // List Book API

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private BookRepository bookRepository;

  private ResultActions callApi(BookListRequestDto bookListRequestDto) throws Exception {
    return mockMvc.perform(
        get("/api/v1/books/list")
            .contentType("application/json")
            .param("filter", bookListRequestDto.getFilter())
            .param("keyword", bookListRequestDto.getKeyword())
            .param("approvalStatus", bookListRequestDto.getApprovalStatus().name())
    );
  }

  @Test
  @DisplayName("저장된 Book 튜플이 없는 경우")
  public void success_onNoBookDataInDB() throws Exception {
    // Given: 유효한 BookSearchRequestDto가 주어진다.
    BookListRequestDto validBookListRequestDto = RequestDto.baseBookSearchRequestDto();

    // When: List Book API를 호출한다.
    ResultActions resultActions = callApi(validBookListRequestDto);

    // Then: Status는 Ok이다.
    resultActions.andExpect(status().isOk());
    // And: ResponseBody로 Book List를 반환한다. (Empty)
    resultActions.andExpect(jsonPath("$").isEmpty());
  }

  @Test
  @DisplayName("저장된 Book 튜플이 있는 경우")
  public void success_onBookDataInDB() throws Exception {
    // Given: 유효한 BookSearchRequestDto가 주어진다.
    BookListRequestDto validBookListRequestDto = RequestDto.baseBookSearchRequestDto();

    // Book 데이터가 30개 주어진다.
    List<Book> mockBookList = MockBook.list(30);

    // And: BookRepository가 mockBookList를 반환
    when(bookRepository.findApprovedBooksByFilterAndKeywordContaining(
        validBookListRequestDto.getFilter(), validBookListRequestDto.getKeyword())
    ).thenReturn(mockBookList);

    // When: List Book API를 호출한다.
    ResultActions resultActions = callApi(validBookListRequestDto);

    // Then: Status는 Ok이다.
    resultActions.andExpect(status().isOk());
    // And: ResponseBody로 Book List를 반환한다.
    resultActions.andExpect(jsonPath("$", hasSize(mockBookList.size())));
  }

  @Test
  @DisplayName("filter가 유효하지 않은 경우")
  public void fail_onInvalidBookSearchRequestDto_withFilter() throws Exception {
    // Given: 유효하지 않은 BookSearchRequestDto가 주어진다.
    BookListRequestDto validBookListRequestDto = RequestDto.baseBookSearchRequestDto();
    validBookListRequestDto.setFilter("invalid");

    // When: List Book API를 호출한다.
    ResultActions resultActions = callApi(validBookListRequestDto);

    // Then: Status는 Bad Request이다.
    resultActions.andExpect(status().isBadRequest());
    // And: ResponseBody로 message와 details를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(BAD_REQUEST.getMessage())));
    resultActions.andExpect(jsonPath("$.details.filter", is(Message.INVALID_FILTER)));
  }

  @Test
  @DisplayName("keyword가 유효하지 않은 경우")
  public void fail_onInvalidBookSearchRequestDto_withKeyword() throws Exception {
    // Given: 유효하지 않은 BookSearchRequestDto가 주어진다.
    BookListRequestDto validBookListRequestDto = RequestDto.baseBookSearchRequestDto();
    validBookListRequestDto.setKeyword(null);

    // When: List Book API를 호출한다.
    ResultActions resultActions = callApi(validBookListRequestDto);

    // Then: Status는 Bad Request이다.
    resultActions.andExpect(status().isBadRequest());
    // And: ResponseBody로 message와 details를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(BAD_REQUEST.getMessage())));
    resultActions.andExpect(jsonPath("$.details.keyword", containsString("null")));
  }
}
