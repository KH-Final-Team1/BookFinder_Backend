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
import com.kh.bookfinder.book.enums.ApprovalStatus;
import com.kh.bookfinder.book.enums.BookListFilter;
import com.kh.bookfinder.book.repository.BookRepository;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.helper.MockBook;
import com.kh.bookfinder.helper.RequestDto;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
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
            .param("status", bookListRequestDto.getStatus())
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
    // And: Book 데이터가 30개 주어진다.
    List<Book> mockBookList = MockBook.list(30);

    // And: BookRepository가 mockBookList를 반환
    when(
        bookRepository.findBy(
            BookListFilter.fromStringIgnoreCase(validBookListRequestDto.getFilter()),
            validBookListRequestDto.getKeyword(),
            ApprovalStatus.fromStringIgnoreCase(validBookListRequestDto.getStatus()))
    ).thenReturn(mockBookList);

    // When: List Book API를 호출한다.
    ResultActions resultActions = callApi(validBookListRequestDto);

    // Then: Status는 Ok이다.
    resultActions.andExpect(status().isOk());
    // And: ResponseBody로 Book List를 반환한다.
    resultActions.andExpect(jsonPath("$", hasSize(mockBookList.size())));
  }

  @Test
  @DisplayName("filter가 name일 때")
  public void success_onValidBookListRequestDto_withFilterIsName() throws Exception {
    // Given: filter가 name, keyword가 '자바', status가 approve인 BookSearchRequestDto가 주어진다.
    BookListRequestDto validBookListRequestDto = RequestDto.baseBookSearchRequestDto();
    validBookListRequestDto.setKeyword("자바");
    // And: Book 데이터가 30개 주어진다.
    List<Book> mockBookList = MockBook.list(30);
    // And: 그 중 10개의 데이터는 name에 '자바'가 포함된다.
    updateOnFilter(mockBookList, 10, BookListFilter.NAME, "자바");
    List<Book> expected = mockBookList.stream()
        .filter(x -> x.getName().contains(validBookListRequestDto.getKeyword()))
        .filter(x -> x.getApprovalStatus().name().equalsIgnoreCase(validBookListRequestDto.getStatus()))
        .collect(Collectors.toList());

    // And: BookRepository가 mockBookList를 반환
    when(bookRepository.findAll()).thenReturn(mockBookList);
    when(
        bookRepository.findBy(
            BookListFilter.fromStringIgnoreCase(validBookListRequestDto.getFilter()),
            validBookListRequestDto.getKeyword(),
            ApprovalStatus.fromStringIgnoreCase(validBookListRequestDto.getStatus()))
    ).thenReturn(expected);

    // When: List Book API를 호출한다.
    ResultActions resultActions = callApi(validBookListRequestDto);

    // Then: Status는 Ok이다.
    resultActions.andExpect(status().isOk());
    // And: ResponseBody로 Book List를 반환한다.
    resultActions.andExpect(jsonPath("$", hasSize(expected.size())));
  }

  @Test
  @DisplayName("filter가 authors일 때")
  public void success_onValidBookListRequestDto_withFilterIsAuthors() throws Exception {
    // Given: filter가 authors, keyword가 '저자', status가 approve인 BookSearchRequestDto가 주어진다.
    BookListRequestDto validBookListRequestDto = RequestDto.baseBookSearchRequestDto();
    validBookListRequestDto.setFilter("authors");
    validBookListRequestDto.setKeyword("저자");
    // And: Book 데이터가 30개 주어진다.
    List<Book> mockBookList = MockBook.list(30);
    // And: 그 중 5개의 데이터는 authors에 '저자'가 포함된다.
    updateOnFilter(mockBookList, 5, BookListFilter.AUTHORS, "저자");
    List<Book> expected = mockBookList.stream()
        .filter(x -> x.getName().contains(validBookListRequestDto.getKeyword()))
        .filter(x -> x.getApprovalStatus().name().equalsIgnoreCase(validBookListRequestDto.getStatus()))
        .collect(Collectors.toList());

    // And: BookRepository가 mockBookList를 반환
    when(bookRepository.findAll()).thenReturn(mockBookList);
    when(
        bookRepository.findBy(
            BookListFilter.fromStringIgnoreCase(validBookListRequestDto.getFilter()),
            validBookListRequestDto.getKeyword(),
            ApprovalStatus.fromStringIgnoreCase(validBookListRequestDto.getStatus()))
    ).thenReturn(expected);

    // When: List Book API를 호출한다.
    ResultActions resultActions = callApi(validBookListRequestDto);

    // Then: Status는 Ok이다.
    resultActions.andExpect(status().isOk());
    // And: ResponseBody로 Book List를 반환한다.
    resultActions.andExpect(jsonPath("$", hasSize(expected.size())));
  }

  @Test
  @DisplayName("filter가 publisher일 때")
  public void success_onValidBookListRequestDto_withFilterIsPublisher() throws Exception {
    // Given: filter가 publisher, keyword가 '길벗', status가 approve인 BookSearchRequestDto가 주어진다.
    BookListRequestDto validBookListRequestDto = RequestDto.baseBookSearchRequestDto();
    validBookListRequestDto.setFilter("publisher");
    validBookListRequestDto.setKeyword("길벗");
    // And: Book 데이터가 30개 주어진다.
    List<Book> mockBookList = MockBook.list(30);
    // And: 그 중 20개의 데이터는 authors에 '길벗'가 포함된다.
    updateOnFilter(mockBookList, 20, BookListFilter.PUBLISHER, "길벗");
    List<Book> expected = mockBookList.stream()
        .filter(x -> x.getName().contains(validBookListRequestDto.getKeyword()))
        .filter(x -> x.getApprovalStatus().name().equalsIgnoreCase(validBookListRequestDto.getStatus()))
        .collect(Collectors.toList());

    // And: BookRepository가 mockBookList를 반환
    when(bookRepository.findAll()).thenReturn(mockBookList);
    when(
        bookRepository.findBy(
            BookListFilter.fromStringIgnoreCase(validBookListRequestDto.getFilter()),
            validBookListRequestDto.getKeyword(),
            ApprovalStatus.fromStringIgnoreCase(validBookListRequestDto.getStatus()))
    ).thenReturn(expected);

    // When: List Book API를 호출한다.
    ResultActions resultActions = callApi(validBookListRequestDto);

    // Then: Status는 Ok이다.
    resultActions.andExpect(status().isOk());
    // And: ResponseBody로 Book List를 반환한다.
    resultActions.andExpect(jsonPath("$", hasSize(expected.size())));
  }

  @Test
  @DisplayName("status가 null일 때")
  public void success_onValidBookListRequestDto_withStatusIsNull() throws Exception {
    // Given: filter가 name, keyword가 '' status가 null인 BookSearchRequestDto가 주어진다.
    BookListRequestDto validBookListRequestDto = RequestDto.baseBookSearchRequestDto();
    validBookListRequestDto.setStatus(null);
    // And: Book 데이터가 30개 주어진다.
    List<Book> mockBookList = MockBook.list(30);
    // And: 그 중 11개의 데이터는 status가 approve가 아니다.
    updateOnStatus(mockBookList, 11, ApprovalStatus.WAIT);
    List<Book> expected = mockBookList.stream()
        .filter(x -> x.getName().contains(validBookListRequestDto.getKeyword()))
        .filter(x -> !x.getApprovalStatus().equals(ApprovalStatus.APPROVE))
        .collect(Collectors.toList());

    // And: BookRepository가 mockBookList를 반환
    when(bookRepository.findAll()).thenReturn(mockBookList);
    when(
        bookRepository.findBy(
            BookListFilter.fromStringIgnoreCase(validBookListRequestDto.getFilter()),
            validBookListRequestDto.getKeyword(),
            ApprovalStatus.fromStringIgnoreCase(validBookListRequestDto.getStatus())
        )
    ).thenReturn(expected);

    // When: List Book API를 호출한다.
    ResultActions resultActions = callApi(validBookListRequestDto);

    // Then: Status는 Ok이다.
    resultActions.andExpect(status().isOk());
    // And: ResponseBody로 Book List를 반환한다.
    resultActions.andExpect(jsonPath("$", hasSize(expected.size())));
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
    resultActions.andExpect(jsonPath("$.details.filter", containsString(Message.INVALID_FILTER)));
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

  @Test
  @DisplayName("status가 유효하지 않은 경우")
  public void fail_onInvalidBookSearchRequestDto_withStatus() throws Exception {
    // Given: 유효하지 않은 BookSearchRequestDto가 주어진다.
    BookListRequestDto validBookListRequestDto = RequestDto.baseBookSearchRequestDto();
    validBookListRequestDto.setStatus("invalid");

    // When: List Book API를 호출한다.
    ResultActions resultActions = callApi(validBookListRequestDto);

    // Then: Status는 Bad Request이다.
    resultActions.andExpect(status().isBadRequest());
    // And: ResponseBody로 message와 details를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(BAD_REQUEST.getMessage())));
    resultActions.andExpect(jsonPath("$.details.status", containsString(Message.INVALID_APPROVAL_STATUS)));
  }

  private void updateOnFilter(List<Book> mockBookList, int count, BookListFilter filter, String updateKeyword) {
    HashSet<Integer> indexes = new HashSet<>();
    while (indexes.size() < count) {
      indexes.add(new Random().nextInt(mockBookList.size()));
    }

    for (int index : indexes) {
      Book target = mockBookList.get(index);
      String updateContent = "update " + updateKeyword + " " + index;
      if (filter == BookListFilter.NAME) {
        target.setName(updateContent);
      } else if (filter == BookListFilter.AUTHORS) {
        target.setAuthors(updateContent);
      } else if (filter == BookListFilter.PUBLISHER) {
        target.setPublisher(updateContent);
      }

      mockBookList.set(index, target);
    }
  }

  private void updateOnStatus(List<Book> mockBookList, int count, ApprovalStatus updateStatus) {
    HashSet<Integer> indexes = new HashSet<>();
    while (indexes.size() < count) {
      indexes.add(new Random().nextInt(mockBookList.size()));
    }

    for (int index : indexes) {
      Book target = mockBookList.get(index);
      target.setApprovalStatus(updateStatus);

      mockBookList.set(index, target);
    }
  }
}
