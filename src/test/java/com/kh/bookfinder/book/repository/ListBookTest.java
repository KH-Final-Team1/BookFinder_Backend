package com.kh.bookfinder.book.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.kh.bookfinder.book.entity.Book;
import com.kh.bookfinder.book.enums.ApprovalStatus;
import com.kh.bookfinder.book.enums.BookListFilter;
import com.kh.bookfinder.global.config.QuerydslConfig;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@DataJpaTest
@Import(QuerydslConfig.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class ListBookTest {

  @Autowired
  private BookRepository bookRepository;

  @Test
  @Sql("classpath:forBookListTest.sql")
  public void test_repositoryLayer_onFilter() {
    // name=test book name {index} / authors=test authors {index} / publisher=test publisher {index}
    List<Book> all = bookRepository.findAll();

    // Test 1: filter=name / keyword=test / status=APPROVE
    List<Book> expected = all.stream()
        .filter(x -> x.getName().contains("test") && x.getApprovalStatus().equals(ApprovalStatus.APPROVE))
        .collect(Collectors.toList());
    List<Book> actual = bookRepository.findBy(BookListFilter.NAME, "test", ApprovalStatus.APPROVE);
    assertThat(expected.size()).isEqualTo(actual.size());

    // Test 2: filter=authors / keyword=1 / status=APPROVE
    expected = all.stream()
        .filter(x -> x.getName().contains("1") && x.getApprovalStatus().equals(ApprovalStatus.APPROVE))
        .collect(Collectors.toList());
    actual = bookRepository.findBy(BookListFilter.AUTHORS, "1", ApprovalStatus.APPROVE);
    assertThat(expected.size()).isEqualTo(actual.size());

    // Test 3: filter=publisher / keyword= / status=APPROVE
    expected = all.stream()
        .filter(x -> x.getName().contains(" ") && x.getApprovalStatus().equals(ApprovalStatus.APPROVE))
        .collect(Collectors.toList());
    actual = bookRepository.findBy(BookListFilter.PUBLISHER, " ", ApprovalStatus.APPROVE);
    assertThat(expected.size()).isEqualTo(actual.size());
  }
}
