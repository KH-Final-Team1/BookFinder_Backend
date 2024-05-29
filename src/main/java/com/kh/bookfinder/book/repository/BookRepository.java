package com.kh.bookfinder.book.repository;

import com.kh.bookfinder.book.entity.ApprovalStatus;
import com.kh.bookfinder.book.entity.Book;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

  Optional<Book> findByIsbn(Long isbn);

  Optional<Book> findByIsbnAndApprovalStatus(Long isbn, ApprovalStatus approvalStatus);

  List<Book> findByPublisherContainingAndApprovalStatus(String keyword, ApprovalStatus approvalStatus);

  List<Book> findByNameContainingAndApprovalStatus(String keyword, ApprovalStatus approvalStatus);

  List<Book> findByAuthorsContainingAndApprovalStatus(String keyword, ApprovalStatus approvalStatus);

  Page<Book> findByNameContainingAndApprovalStatusIn(String keyword, List<ApprovalStatus> approvalStatuses,
      Pageable pageable);

  Page<Book> findByAuthorsContainingAndApprovalStatusIn(String keyword, List<ApprovalStatus> approvalStatuses,
      Pageable pageable);

  Page<Book> findByPublisherContainingAndApprovalStatusIn(String keyword, List<ApprovalStatus> approvalStatuses,
      Pageable pageable);
}