package com.kh.bookfinder.controller;

import com.kh.bookfinder.constants.Message;
import com.kh.bookfinder.dto.SearchDto;
import com.kh.bookfinder.entity.Book;
import com.kh.bookfinder.service.BookService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookController {

  @Autowired
  private BookService bookService;

  @GetMapping("/search")
  public ResponseEntity<List<Book>> selectList(@Valid SearchDto requestParam) {
    List<Book> bookList = bookService.selectList(requestParam);
    return ResponseEntity.ok().body(bookList);
  }

  @GetMapping(value = "api/v1/books/{isbn}", produces = "application/json;charset=UTF-8")
  public ResponseEntity<Object> selectBookRequestIsbn(@PathVariable(name = "isbn") Long isbn) throws JSONException {
    if ((long) (Math.log10(isbn) + 2) == 13) {
      JSONObject responseBody = new JSONObject();
      responseBody.put("message", Message.INVALID_ISBN_DIGITS);
      return ResponseEntity.badRequest().body(responseBody.toString());
    }
    Optional<Book> book = bookService.findBook(isbn);
    return ResponseEntity.ok().body(book);
  }
}
