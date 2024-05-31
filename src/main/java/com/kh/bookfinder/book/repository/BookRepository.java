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

  List<Book> findByNameContainingAndApprovalStatus(String keyword, ApprovalStatus approvalStatus);

  List<Book> findByAuthorsContainingAndApprovalStatus(String keyword, ApprovalStatus approvalStatus);

  List<Book> findByPublisherContainingAndApprovalStatus(String keyword, ApprovalStatus approvalStatus);

  default List<Book> findApprovedBooksByFilterAndKeywordContaining(String filter, String keyword) {
    if (filter.equals("name")) {
      return findByNameContainingAndApprovalStatus(keyword, ApprovalStatus.APPROVE);
    }
    if (filter.equals("authors")) {
      return findByAuthorsContainingAndApprovalStatus(keyword, ApprovalStatus.APPROVE);
    }
    return findByPublisherContainingAndApprovalStatus(keyword, ApprovalStatus.APPROVE);
  }

  Page<Book> findByNameContainingAndApprovalStatusNot(String keyword, ApprovalStatus approvalStatus, Pageable pageable);

  Page<Book> findByAuthorsContainingAndApprovalStatusNot(String keyword, ApprovalStatus approvalStatus,
      Pageable pageable);

  Page<Book> findByPublisherContainingAndApprovalStatusNot(String keyword, ApprovalStatus approvalStatus,
      Pageable pageable);

  default List<Book> findNotApprovedBooksByFilterAndKeywordContaining(String filter, String keyword,
      Pageable pageable) {
    if (filter.equals("name")) {
      return findByNameContainingAndApprovalStatusNot(keyword, ApprovalStatus.APPROVE, pageable).getContent();
    }
    if (filter.equals("authors")) {
      return findByAuthorsContainingAndApprovalStatusNot(keyword, ApprovalStatus.APPROVE, pageable).getContent();
    }
    return findByPublisherContainingAndApprovalStatusNot(keyword, ApprovalStatus.APPROVE, pageable).getContent();
  }
}