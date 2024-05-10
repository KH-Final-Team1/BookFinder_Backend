package com.kh.bookfinder.controller;

import com.kh.bookfinder.dto.SearchDto;
import com.kh.bookfinder.entity.Book;
import com.kh.bookfinder.service.BookService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
}
