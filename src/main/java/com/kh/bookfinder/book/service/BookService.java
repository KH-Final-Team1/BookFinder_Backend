package com.kh.bookfinder.book.service;

import com.kh.bookfinder.book.dto.SearchDto;
import com.kh.bookfinder.book.entity.Book;
import com.kh.bookfinder.book.repository.BookRepository;
import com.kh.bookfinder.global.constants.Message;
import java.util.List;
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
    if (requestParam.getFilter().equals("name")) {
      return bookRepository.findByNameContaining(requestParam.getKeyword());
    } else if (requestParam.getFilter().equals("authors")) {
      return bookRepository.findByAuthorsContaining(requestParam.getKeyword());
    }
    return bookRepository.findByPublisherContaining(requestParam.getKeyword());
  }

  public List<Book> selectListWait(SearchDto requestParam) {
    List<Book> bookList;
    if (requestParam.getFilter().equals("name")) {
      bookList = bookRepository.findByNameContainingAndApprovalStatus(requestParam.getKeyword(), "WAIT");
    } else {
      bookList = bookRepository.findByAuthorsContainingAndApprovalStatus(requestParam.getKeyword(), "WAIT");
    }
    if (bookList.isEmpty()) {
      throw new ResourceNotFoundException(Message.NOT_FOUND_WAIT);
    }
    return bookList;
  }

  public List<Book> findAllByApprovalStatus(String approvalStatus) {
    List<Book> bookList = bookRepository.findAllByApprovalStatus(approvalStatus);
    if (bookList.isEmpty()) {
      throw new ResourceNotFoundException(Message.NOT_FOUND_WAIT);
    }
    return bookList;
  }
}