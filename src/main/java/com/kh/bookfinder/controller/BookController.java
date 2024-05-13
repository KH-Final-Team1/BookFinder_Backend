package com.kh.bookfinder.controller;

import com.kh.bookfinder.constants.Message;
import com.kh.bookfinder.dto.SearchDto;
import com.kh.bookfinder.dto.StatusDto;
import com.kh.bookfinder.entity.Book;
import com.kh.bookfinder.exception.InvalidFieldException;
import com.kh.bookfinder.service.BookService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/books")
public class BookController {

  private final BookService bookService;
  private final int ISBN_DIGITS = 13;

  @GetMapping("/search")
  public ResponseEntity<List<Book>> selectList(@Valid SearchDto requestParam) {
    List<Book> bookList = bookService.selectList(requestParam);
    return ResponseEntity.ok().body(bookList);
  }

  @GetMapping(value = "/{isbn}", produces = "application/json;charset=UTF-8")
  public ResponseEntity<Object> GetBook(@PathVariable(name = "isbn") Long isbn) throws JSONException {
    if ((long) (Math.log10(isbn) + 2) == ISBN_DIGITS) {
      JSONObject responseBody = new JSONObject();
      responseBody.put("message", Message.INVALID_ISBN_DIGITS);
      return ResponseEntity.badRequest().body(responseBody.toString());
    }
    Optional<Book> book = bookService.findBook(isbn);
    return ResponseEntity.ok().body(book);
  }

  @GetMapping("/wait/list")
  public ResponseEntity<List<Book>> GetBookRequests(@Valid SearchDto requestParam) {
    if (!requestParam.getKeyword().isEmpty()) {
      List<Book> bookList = bookService.selectListWait(requestParam);
      return ResponseEntity.ok().body(bookList);
    }
    List<Book> bookList = bookService.findAllByApprovalStatus("WAIT");
    return ResponseEntity.ok().body(bookList);
  }

  @PatchMapping(value = "/request", produces = "application/json;charset=UTF-8")
  public ResponseEntity<String> updateBookStatus(@Valid StatusDto statusDto) throws JSONException {
    Book book = bookService.findBook(statusDto.getIsbn())
        .orElseThrow(() -> new InvalidFieldException("ISBN", Message.INVALID_ISBN));
    book.setApprovalStatus(statusDto.getApprovalStatus());
    bookService.saveBook(book);
    JSONObject responseBody = new JSONObject();
    responseBody.put("message", Message.SUCCESS_APPROVE_STATUS);
    return ResponseEntity.ok().body(responseBody.toString());
  }
}
