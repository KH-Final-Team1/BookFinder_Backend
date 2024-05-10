package com.kh.bookfinder.service;

import com.kh.bookfinder.constants.Message;
import com.kh.bookfinder.dto.SearchDto;
import com.kh.bookfinder.entity.Book;
import com.kh.bookfinder.repository.BookRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class BookService {

  @Autowired
  BookRepository bookRepository;

  public Book findBook(Long isbn) {
    return bookRepository.findByIsbn(isbn)
        .orElseThrow(() -> new ResourceNotFoundException(Message.UNSAVED_ISBN));
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