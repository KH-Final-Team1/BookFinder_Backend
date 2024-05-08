package com.kh.bookfinder.controller;

import com.kh.bookfinder.dto.BookDTO;
import com.kh.bookfinder.service.BookService;
import com.kh.bookfinder.entity.Book;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping(value = "api/v1/book/detail/{isbn}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Book> selectBookRequestIsbn(@PathVariable(name = "isbn") @Valid BookDTO bookDTO){
        Book book =  bookService.findByBook(bookDTO);
        return ResponseEntity.ok().body(book);
    }
}