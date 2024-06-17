package com.kh.bookfinder.book.controller;

import com.kh.bookfinder.auth.login.dto.SecurityUserDetails;
import com.kh.bookfinder.book.dto.ApprovalStatusDto;
import com.kh.bookfinder.book.dto.BookListRequestDto;
import com.kh.bookfinder.book.dto.BookRequestDto;
import com.kh.bookfinder.book.entity.Book;
import com.kh.bookfinder.book.service.BookService;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.user.entity.User;
import com.kh.bookfinder.user.entity.UserRole;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
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
@Validated
public class BookController {

  private final BookService bookService;

  @GetMapping("/list")
  public ResponseEntity<List<Book>> getBooks(@Valid BookListRequestDto requestParam) {
    List<Book> books = bookService.getBooks(requestParam);
    return ResponseEntity.ok().body(books);
  }

  @GetMapping("/{isbn}")
  public ResponseEntity<Book> getBook(@Valid @PathVariable(name = "isbn")
  @Range(min = 1000000000000L, max = 9999999999999L, message = Message.INVALID_ISBN_DIGITS) Long isbn) {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User serviceUser = principal instanceof SecurityUserDetails
        ? ((SecurityUserDetails) principal).getServiceUser()
        : User.builder().role(UserRole.ROLE_GUEST).build();

    Book book = bookService.getBook(serviceUser, isbn);
    return ResponseEntity.ok().body(book);
  }

  @PatchMapping("/{isbn}")
  public ResponseEntity<Map<String, String>> updateBookStatus(@PathVariable(name = "isbn") Long isbn,
      @Valid ApprovalStatusDto statusDto) {
    bookService.updateStatus(isbn, statusDto);
    return ResponseEntity.ok().body(Map.of("message", Message.UPDATE_APPROVAL_STATUS));
  }

  @PostMapping
  public ResponseEntity<Map<String, String>> createBook(@RequestBody @Valid BookRequestDto bookRequestDto) {
    bookService.requestBook(bookRequestDto);
    return ResponseEntity.ok().body(Map.of("message", Message.SUCCESS_REQUEST));
  }
}