package com.kh.bookfinder.book.repository;

import com.kh.bookfinder.book.entity.ApprovalStatus;
import com.kh.bookfinder.book.entity.Book;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

  Optional<Book> findByIsbn(Long isbn);

  Optional<Book> findByIsbnAndApprovalStatus(Long isbn, ApprovalStatus approvalStatus);

  List<Book> findByPublisherContainingAndApprovalStatus(String keyword, ApprovalStatus approvalStatus, Sort sort);

  List<Book> findByNameContainingAndApprovalStatus(String keyword, ApprovalStatus approvalStatus, Sort sort);

  List<Book> findByAuthorsContainingAndApprovalStatus(String keyword, ApprovalStatus approvalStatus, Sort sort);

  List<Book> findByNameContaining(String keyword, Sort sort);

  List<Book> findByAuthorsContaining(String keyword, Sort sort);

  List<Book> findByPublisherContaining(String keyword, Sort sort);
}