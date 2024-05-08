package com.kh.bookfinder.controller;

import com.kh.bookfinder.dto.SearchDto;
import com.kh.bookfinder.entity.Book;
import com.kh.bookfinder.service.BookService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
}
