package com.kh.bookfinder.book.repository;

import com.kh.bookfinder.book.entity.Book;
import com.kh.bookfinder.book.enums.ApprovalStatus;
import java.util.List;
import java.util.Optional;
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

  List<Book> findByNameContainingAndApprovalStatusNot(String keyword, ApprovalStatus approvalStatus);

  List<Book> findByAuthorsContainingAndApprovalStatusNot(String keyword, ApprovalStatus approvalStatus);

  List<Book> findByPublisherContainingAndApprovalStatusNot(String keyword, ApprovalStatus approvalStatus);

  default List<Book> findNotApprovedBooksByFilterAndKeywordContaining(String filter, String keyword) {
    if (filter.equals("name")) {
      return findByNameContainingAndApprovalStatusNot(keyword, ApprovalStatus.APPROVE);
    }
    if (filter.equals("authors")) {
      return findByAuthorsContainingAndApprovalStatusNot(keyword, ApprovalStatus.APPROVE);
    }
    return findByPublisherContainingAndApprovalStatusNot(keyword, ApprovalStatus.APPROVE);
  }
}