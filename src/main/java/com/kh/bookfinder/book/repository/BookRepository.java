package com.kh.bookfinder.book.repository;

import com.kh.bookfinder.book.entity.Book;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

  Optional<Book> findByIsbn(Long isbn);

  List<Book> findByPublisherContainingAndApprovalStatus(String keyword, String approvalStatus);

  List<Book> findByNameContainingAndApprovalStatus(String keyword, String approvalStatus);

  List<Book> findByAuthorsContainingAndApprovalStatus(String keyword, String approvalStatus);

  List<Book> findAllByApprovalStatus(String approvalStatus);
}