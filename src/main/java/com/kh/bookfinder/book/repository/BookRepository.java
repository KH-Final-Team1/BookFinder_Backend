package com.kh.bookfinder.book.repository;

import com.kh.bookfinder.book.entity.Book;
import com.kh.bookfinder.book.enums.ApprovalStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, BookQueryRepository {

  Optional<Book> findByIsbn(Long isbn);

  Optional<Book> findByIsbnAndApprovalStatus(Long isbn, ApprovalStatus approvalStatus);
}