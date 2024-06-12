package com.kh.bookfinder.book.controller;

import com.kh.bookfinder.book.dto.ApprovalStatusDto;
import com.kh.bookfinder.book.dto.BookRequestDto;
import com.kh.bookfinder.book.dto.BookSearchRequestDto;
import com.kh.bookfinder.book.entity.Book;
import com.kh.bookfinder.book.service.BookService;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.global.exception.InvalidFieldException;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/books")
public class BookController {

  private final BookService bookService;
  private static final int ISBN_DIGITS = 13;

  @GetMapping("/list")
  public ResponseEntity<List<Book>> getBooks(@Valid BookSearchRequestDto requestParam) {
    List<Book> books = bookService.getBooks(requestParam);
    return ResponseEntity.ok().body(books);
  }

  @GetMapping("/{isbn}")
  public ResponseEntity<Book> getBook(@PathVariable(name = "isbn") Long isbn) {
    if ((long) (Math.log10(isbn) + 1) != ISBN_DIGITS) {
      throw new InvalidFieldException("message", Message.INVALID_ISBN_DIGITS);
    }
    Book book = bookService.findApprovedBook(isbn);
    return ResponseEntity.ok().body(book);
  }

  @PatchMapping("/{isbn}")
  public ResponseEntity<Map<String, String>> updateBookStatus(@PathVariable(name = "isbn") Long isbn,
      @Valid ApprovalStatusDto requestParam) {
    bookService.updateStatus(isbn, requestParam.getApprovalStatus());
    return ResponseEntity.ok().body(Map.of("message", Message.UPDATE_APPROVAL_STATUS));
  }

  @PostMapping
  public ResponseEntity<Map<String, String>> createBook(@RequestBody @Valid BookRequestDto bookRequestDto) {
    bookService.requestBook(bookRequestDto);
    return ResponseEntity.ok().body(Map.of("message", Message.SUCCESS_REQUEST));
  }
}