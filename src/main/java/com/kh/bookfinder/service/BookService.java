package com.kh.bookfinder.service;

import com.kh.bookfinder.constants.Message;
import com.kh.bookfinder.entity.Book;
import com.kh.bookfinder.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookService  {

    @Autowired
    private BookRepository bookRepository;
    public Optional<Book> findBook(Long isbn) {
        Optional<Book> book = bookRepository.findByIsbn(isbn);
        if (book.isEmpty()) {
            throw new ResourceNotFoundException(Message.NOT_FOUND_ISBN);
        }
        return book;

    }

}