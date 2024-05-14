package com.kh.bookfinder.book.controller;

import com.kh.bookfinder.book.dto.SearchDto;
import com.kh.bookfinder.book.entity.Book;
import com.kh.bookfinder.book.service.BookService;
import com.kh.bookfinder.global.constants.Message;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

  private final BookService bookService;

  @GetMapping("/list")
  public ResponseEntity<List<Book>> getBooks(@Valid SearchDto requestParam) {
    List<Book> bookList = bookService.getBooks(requestParam);
    return ResponseEntity.ok().body(bookList);
  }

  @GetMapping(value = "api/v1/books/{isbn}", produces = "application/json;charset=UTF-8")
  public ResponseEntity<Object> selectBookRequestIsbn(@PathVariable(name = "isbn") Long isbn) throws JSONException {
    if ((long) (Math.log10(isbn) + 2) == 13) {
      JSONObject responseBody = new JSONObject();
      responseBody.put("message", Message.INVALID_ISBN_DIGITS);
      return ResponseEntity.badRequest().body(responseBody.toString());
    }
    Book book = bookService.findBook(isbn);
    return ResponseEntity.ok().body(book);
  }

  @GetMapping("/api/v1/request/list")
  public ResponseEntity<List<Book>> selectBookRequestList(@Valid SearchDto requestParam) {
    if (!requestParam.getKeyword().isEmpty()) {
      List<Book> bookList = bookService.selectListWait(requestParam);
      return ResponseEntity.ok().body(bookList);
    }
    List<Book> bookList = bookService.findAllByApprovalStatus("WAIT");
    return ResponseEntity.ok().body(bookList);
  }
}
