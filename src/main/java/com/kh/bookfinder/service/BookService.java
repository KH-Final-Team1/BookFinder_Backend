package com.kh.bookfinder.service;

import com.kh.bookfinder.entity.Book;
import com.kh.bookfinder.repository.BookRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookService {

  @Autowired
  BookRepository bookRepository;

  public Optional<Book> findBook(Long isbn) {
    return bookRepository.findByIsbn(isbn);
  }
}
