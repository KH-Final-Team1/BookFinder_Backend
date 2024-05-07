package com.kh.bookfinder.Service;

import com.kh.bookfinder.entity.Book;
import com.kh.bookfinder.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookService  {

    @Autowired
    private BookRepository bookRepository;

    public Optional<Book> findByIsbn(long isbn) {
        return bookRepository.findById(isbn);
    }
}
