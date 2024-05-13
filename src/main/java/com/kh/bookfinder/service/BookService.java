package com.kh.bookfinder.service;

import com.kh.bookfinder.constants.Message;
import com.kh.bookfinder.dto.SearchDto;
import com.kh.bookfinder.entity.Book;
import com.kh.bookfinder.repository.BookRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookService {

  private final BookRepository bookRepository;

  public Optional<Book> findBook(Long isbn) {
    Optional<Book> book = bookRepository.findByIsbn(isbn);
    if (book.isEmpty()) {
      throw new ResourceNotFoundException(Message.NOT_FOUND_ISBN);
    }
    return book;
  }

  public List<Book> selectList(SearchDto requestParam) {
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

  public void saveBook(Book book) {
    bookRepository.save(book);
  }
}