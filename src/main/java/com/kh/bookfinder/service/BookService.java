package com.kh.bookfinder.service;

import com.kh.bookfinder.constants.Message;
import com.kh.bookfinder.dto.BookRequestDTO;
import com.kh.bookfinder.exception.InvalidFieldException;
import com.kh.bookfinder.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookService  {

    @Autowired
    private BookRepository bookRepository;

    public void IsbnDuplicate(BookRequestDTO bookRequestDTO) {
        this.bookRepository
                .findById(bookRequestDTO.getIsbn())
                .ifPresent((value) -> {
                    throw new InvalidFieldException("isbn", Message.DUPLICATE_ISBN);
                });
    }
}