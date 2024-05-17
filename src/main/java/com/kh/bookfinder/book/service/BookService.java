package com.kh.bookfinder.book.service;

import com.kh.bookfinder.book.dto.SearchDto;
import com.kh.bookfinder.book.entity.Book;
import com.kh.bookfinder.book.repository.BookRepository;
import com.kh.bookfinder.global.constants.Message;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookService {

  private final BookRepository bookRepository;

  public Book findBook(Long isbn) {
    return bookRepository.findByIsbn(isbn)
        .orElseThrow(() -> new ResourceNotFoundException(Message.UNSAVED_ISBN));
  }

  public List<Book> getBooks(SearchDto requestParam) {
    List<Book> books;
    if (requestParam.getFilter().equals("name")) {
      books = bookRepository.findByNameContainingAndApprovalStatus(requestParam.getKeyword(),
          requestParam.getApprovalStatus());
    } else if (requestParam.getFilter().equals("authors")) {
      books = bookRepository.findByAuthorsContainingAndApprovalStatus(requestParam.getKeyword(),
          requestParam.getApprovalStatus());
    } else {
      books = bookRepository.findByPublisherContainingAndApprovalStatus(requestParam.getKeyword(),
          requestParam.getApprovalStatus());
    }
    if (books.isEmpty()) {
      throw new ResourceNotFoundException(Message.NOT_FOUND_BOOK);
    }
    return books;
  }

  @Transactional
  public void updateStatus(Long isbn, String approvalStatus) {
    Book book = findBook(isbn);
    book.setApprovalStatus(approvalStatus);
  }

  @Transactional
  public void saveBook(Book book) {
    bookRepository.save(book);
  }
}