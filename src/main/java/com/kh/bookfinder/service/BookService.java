package com.kh.bookfinder.service;

import com.kh.bookfinder.dto.SearchDto;
import com.kh.bookfinder.entity.Book;
import com.kh.bookfinder.repository.BookRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookService {

  private final BookRepository bookRepository;

  public Optional<Book> findBook(Long isbn) {
    return bookRepository.findByIsbn(isbn);
  }

  public List<Book> getBooks(SearchDto requestParam) {
    if (requestParam.getFilter().equals("name")) {
      return bookRepository.findByNameContaining(requestParam.getKeyword());
    } else if (requestParam.getFilter().equals("authors")) {
      return bookRepository.findByAuthorsContaining(requestParam.getKeyword());
    }
    return bookRepository.findByPublisherContaining(requestParam.getKeyword());
  }
}