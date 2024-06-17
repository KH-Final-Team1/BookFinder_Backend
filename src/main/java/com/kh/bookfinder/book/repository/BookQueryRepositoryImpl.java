package com.kh.bookfinder.book.repository;

import static com.kh.bookfinder.book.entity.QBook.book;

import com.kh.bookfinder.book.entity.Book;
import com.kh.bookfinder.book.enums.ApprovalStatus;
import com.kh.bookfinder.book.enums.BookListFilter;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BookQueryRepositoryImpl implements BookQueryRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<Book> findBy(BookListFilter filter, String keyword, ApprovalStatus status) {
    return queryFactory.selectFrom(book)
        .where(
            filterContainingKeyword(filter, keyword),
            statusCondition(status))
        .fetch();
  }

  private BooleanExpression filterContainingKeyword(BookListFilter filter, String keyword) {
    if (filter == BookListFilter.NAME) {
      return book.name.contains(keyword);
    }
    if (filter == BookListFilter.AUTHORS) {
      return book.authors.contains(keyword);
    }
    if (filter == BookListFilter.PUBLISHER) {
      return book.publisher.contains(keyword);
    }
    return null;
  }

  private BooleanExpression statusCondition(ApprovalStatus status) {
    if (status == null) {
      return book.approvalStatus.ne(ApprovalStatus.APPROVE);
    }
    if (status == ApprovalStatus.APPROVE) {
      return book.approvalStatus.eq(ApprovalStatus.APPROVE);
    }
    if (status == ApprovalStatus.REJECT) {
      return book.approvalStatus.eq(ApprovalStatus.REJECT);
    }
    if (status == ApprovalStatus.WAIT) {
      return book.approvalStatus.eq(ApprovalStatus.WAIT);
    }
    return null;
  }
}
