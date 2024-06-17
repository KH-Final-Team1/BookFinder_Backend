package com.kh.bookfinder.book.repository;

import com.kh.bookfinder.book.entity.Book;
import com.kh.bookfinder.book.enums.ApprovalStatus;
import com.kh.bookfinder.book.enums.BookListFilter;
import java.util.List;

public interface BookQueryRepository {

  List<Book> findBy(BookListFilter filter, String keyword, ApprovalStatus status);
}
