package com.kh.bookfinder.service;

import com.kh.bookfinder.constants.Message;
import com.kh.bookfinder.dto.SearchDto;
import com.kh.bookfinder.entity.Book;
import com.kh.bookfinder.repository.BookRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class BookService {

  @Autowired
  BookRepository bookRepository;

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
}